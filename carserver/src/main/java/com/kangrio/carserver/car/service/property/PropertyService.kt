package com.kangrio.carserver.car.service.property

import android.car.FuelType
import android.car.PortLocationType
import android.car.VehicleAreaSeat
import android.car.VehicleAreaType
import android.car.VehicleGear
import android.car.VehicleIgnitionState
import android.car.VehiclePropertyIds
import android.car.VehicleUnit
import android.car.hardware.CarPropertyConfig
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyEvent
import android.car.hardware.property.EvChargingConnectorType
import android.car.hardware.property.ICarProperty
import android.car.hardware.property.ICarPropertyEventListener
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.SystemClock
import android.os.SystemProperties
import com.kangrio.carserver.byd.PropertyUpdater
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getOrDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

class PropertyService : ICarProperty.Stub() {
    private val callbacks = RemoteCallbackList<ICarPropertyEventListener>()
    private val callbackProps = ConcurrentHashMap<IBinder, MutableSet<Int>>()
    private val values = ConcurrentHashMap<Int, Any>()
    private val configs: List<CarPropertyConfig<*>>
    private val fireIntervalMs = 1_000L
    private val fireCache = ConcurrentHashMap<Int, Int>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        values.putAll(defaultValues())
        configs = values.map { (propId, value) -> configFor(propId, value) }
        LogUtils.i(
            TAG,
            "init profile=${values[VehiclePropertyIds.INFO_MODEL]} props=${configs.size}"
        )
        scope.launch {
            while (isActive) {
                if (fireCache.isNotEmpty()) {
                    val fires = fireCache.toMap()
                    fireCache.clear()

                    fires.forEach { (propId, areaId) ->
                        fire(propId, areaId)
                    }
                }
                delay(fireIntervalMs.milliseconds)
            }
        }
    }

    override fun registerListener(
        propId: Int,
        rate: Float,
        callback: ICarPropertyEventListener?
    ) {
        LogUtils.i(TAG, "registerListener propId=$propId rate=$rate callback=$callback")
        if (callback == null || !values.containsKey(propId)) return
        callbacks.register(callback)
        callbackProps.getOrPut(callback.asBinder()) { mutableSetOf() }.add(propId)
        callback.onEvent(listOf(changeEvent(propId, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)))
    }

    override fun unregisterListener(propId: Int, callback: ICarPropertyEventListener?) {
        LogUtils.i(TAG, "unregisterListener propId=$propId callback=$callback")
        if (callback == null) return
        val binder = callback.asBinder()
        callbackProps[binder]?.remove(propId)
        if (callbackProps[binder].isNullOrEmpty()) {
            callbackProps.remove(binder)
            callbacks.unregister(callback)
        }
    }

    override fun getPropertyList(): List<CarPropertyConfig<*>> {
        LogUtils.i(TAG, "getPropertyList count=${configs.size}")
        return configs
    }

    override fun getProperty(prop: Int, zone: Int): CarPropertyValue<*>? {
        val currentValue = if (values.containsKey(prop)) currentValue(prop, zone) else null
        LogUtils.i(TAG, "getProperty prop=$prop zone=$zone, result=${currentValue?.value}")
        return currentValue
    }

    override fun setProperty(prop: CarPropertyValue<*>?) {
        LogUtils.i(
            TAG,
            "setProperty prop=${prop?.propertyId} area=${prop?.areaId} value=${prop?.value}"
        )
        if (prop == null || prop.value == null) return
        values[prop.propertyId] = prop.value
        fireCache[prop.propertyId] = prop.areaId
    }

    override fun getReadPermission(propId: Int): String? {
        LogUtils.i(TAG, "getReadPermission propId=$propId")
        return null
    }

    override fun getWritePermission(propId: Int): String? {
        LogUtils.i(TAG, "getWritePermission propId=$propId")
        return null
    }

    private fun currentValue(propId: Int, areaId: Int): CarPropertyValue<Any> {
        return CarPropertyValue(
            propId,
            areaId,
            CarPropertyValue.STATUS_AVAILABLE,
            SystemClock.elapsedRealtimeNanos(),
            values[propId]
        )
    }

    private fun fire(propId: Int, areaId: Int) {
        val event = listOf(changeEvent(propId, areaId))
        val count = callbacks.beginBroadcast()
        try {
            for (i in 0 until count) {
                val listener = callbacks.getBroadcastItem(i)
                if (callbackProps[listener.asBinder()]?.contains(propId) == true) {
                    listener.onEvent(event)
                }
            }
        } finally {
            callbacks.finishBroadcast()
        }
    }

    private fun changeEvent(propId: Int, areaId: Int): CarPropertyEvent {
        return CarPropertyEvent(
            CarPropertyEvent.PROPERTY_EVENT_PROPERTY_CHANGE,
            currentValue(propId, areaId)
        )
    }

    private fun configFor(propId: Int, value: Any): CarPropertyConfig<*> {
        val valueClass = when (value) {
            is Boolean -> Boolean::class.javaObjectType
            is Float -> Float::class.javaObjectType
            is Int -> Int::class.javaObjectType
            is Long -> Long::class.javaObjectType
            is Double -> Double::class.javaObjectType
            is String -> String::class.java
            is CharSequence -> CharSequence::class.java

            is IntArray -> IntArray::class.java
            is FloatArray -> FloatArray::class.java
            is LongArray -> LongArray::class.java
            is ByteArray -> ByteArray::class.java
            is ShortArray -> ShortArray::class.java
            is DoubleArray -> DoubleArray::class.java
            is CharArray -> CharArray::class.java

            is Array<*> -> value.javaClass

            else -> value.javaClass
        }

        return CarPropertyConfig.newBuilder(
            valueClass,
            propId,
            VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL
        )
            .setAccess(CarPropertyConfig.VEHICLE_PROPERTY_ACCESS_READ_WRITE)
            .setChangeMode(changeModeFor(propId))
            .setMinSampleRate(if (isContinuous(propId)) 1f else 0f)
            .setMaxSampleRate(if (isContinuous(propId)) 10f else 0f)
            .build()
    }

    private fun changeModeFor(propId: Int): Int {
        return if (isContinuous(propId)) {
            CarPropertyConfig.VEHICLE_PROPERTY_CHANGE_MODE_CONTINUOUS
        } else {
            CarPropertyConfig.VEHICLE_PROPERTY_CHANGE_MODE_ONCHANGE
        }
    }

    private fun isContinuous(propId: Int): Boolean {
        return propId == VehiclePropertyIds.EV_BATTERY_LEVEL ||
                propId == VehiclePropertyIds.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE ||
                propId == VehiclePropertyIds.PERF_VEHICLE_SPEED ||
                propId == VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY ||
                propId == VehiclePropertyIds.PERF_ODOMETER ||
                propId == VehiclePropertyIds.RANGE_REMAINING
    }

    private fun defaultValues(): Map<Int, Any> {
        /**
         * 289410887
         * GENERAL_SAFETY_REGULATION_COMPLIANCE on android 13
         * INFO_EXTERIOR_DIMENSIONS
         */
        val vin = buildString(17) { repeat(17) { append("ABCDEFGHJKLMNPRSTUVWXYZ0123456789".random()) } }
        val vehicleVin = VehicleStore.VEHICLE_VIN.getOrDefault(
            VehicleStore,
            vin
        )
        val vehicleName = VehicleStore.VEHICLE_NAME.getOrDefault(
            VehicleStore,
            "BYD ${vin.substring(0, 4)}"
        )
        return linkedMapOf(
            VehiclePropertyIds.INFO_MODEL_YEAR to 2023,
            VehiclePropertyIds.INFO_EV_BATTERY_CAPACITY to PropertyUpdater.DEFAULT_BATTERY_CAPACITY_WH,
            VehiclePropertyIds.INFO_EV_CONNECTOR_TYPE to arrayOf(
                Integer.valueOf(EvChargingConnectorType.IEC_TYPE_2_AC),
                Integer.valueOf(EvChargingConnectorType.IEC_TYPE_2_CCS_DC)
            ),
            VehiclePropertyIds.INFO_EV_PORT_LOCATION to PortLocationType.FRONT_RIGHT,
            VehiclePropertyIds.INFO_FUEL_TYPE to arrayOf(Integer.valueOf(FuelType.ELECTRIC)),

            // changeable
            VehiclePropertyIds.INFO_VIN to vehicleVin,
            VehiclePropertyIds.INFO_MAKE to vehicleName,
            VehiclePropertyIds.INFO_MODEL to vehicleName,
            VehiclePropertyIds.INFO_DRIVER_SEAT to VehicleStore.DRIVER_SEAT.getOrDefault(
                VehicleStore,
                VehicleAreaSeat.SEAT_ROW_1_LEFT
            ),
            VehiclePropertyIds.EV_CHARGE_PORT_CONNECTED to VehicleStore.CHARGE_PORT_CONNECTED.getOrDefault(
                VehicleStore,
                false
            ),
            VehiclePropertyIds.GEAR_SELECTION to VehicleStore.CURRENT_GEAR.getOrDefault(
                VehicleStore,
                VehicleGear.GEAR_UNKNOWN
            ),
            VehiclePropertyIds.IGNITION_STATE to VehicleStore.IGNITION_STATE.getOrDefault(
                VehicleStore,
                VehicleIgnitionState.UNDEFINED
            ),
            VehiclePropertyIds.PARKING_BRAKE_ON to VehicleStore.PARKING_BRAKE_ON.getOrDefault(
                VehicleStore,
                false
            ),

            VehiclePropertyIds.DISTANCE_DISPLAY_UNITS to VehicleUnit.KILOMETER,
            VehiclePropertyIds.VEHICLE_SPEED_DISPLAY_UNITS to VehicleUnit.KILOMETERS_PER_HOUR,

            VehiclePropertyIds.PERF_VEHICLE_SPEED to (VehicleStore.SPEED_METER_PER_SEC.getOrDefault(
                VehicleStore,
                0f
            )), // m/s
            VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY to (VehicleStore.SPEED_METER_PER_SEC.getOrDefault(
                VehicleStore,
                0f
            )), // m/s

            // on Android 13 maps requested this propertyId, has provided 0 and 1 but still not working to make maps reading vehicleInfo
//            289410887 to 0, //

//            VehiclePropertyIds.INFO_FUEL_CAPACITY to 0f, //
//            VehiclePropertyIds.EV_BATTERY_LEVEL to 48_384f, //
//            VehiclePropertyIds.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE to 0f, //
//            VehiclePropertyIds.EV_BATTERY_DISPLAY_UNITS to VehicleUnit.WATT_HOUR, //
//            VehiclePropertyIds.EV_CHARGE_PORT_OPEN to false, //
//            VehiclePropertyIds.RANGE_REMAINING to 336f, //
//            VehiclePropertyIds.PERF_ODOMETER to 12_345f, //
//            VehiclePropertyIds.CURRENT_GEAR to VehicleGear.GEAR_DRIVE, //
//            VehiclePropertyIds.NIGHT_MODE to false, //
//            VehiclePropertyIds.ENV_OUTSIDE_TEMPERATURE to 40f, //
//            VehiclePropertyIds.HVAC_TEMPERATURE_CURRENT to 25f, //
//            VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS to VehicleUnit.CELSIUS, //
        )
    }
}