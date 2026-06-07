package com.kangrio.carserver.service

import android.car.EvConnectorType
import android.car.FuelType
import android.car.PortLocationType
import android.car.VehicleAreaSeat
import android.car.VehicleAreaType
import android.car.VehiclePropertyIds
import android.car.VehicleUnit
import android.car.hardware.CarPropertyConfig
import android.car.hardware.CarPropertyValue
import android.car.hardware.CarSensorEvent
import android.car.hardware.property.CarPropertyEvent
import android.car.hardware.property.ICarProperty
import android.car.hardware.property.ICarPropertyEventListener
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.SystemClock
import android.util.Log
import com.kangrio.carserver.TAG
import java.util.concurrent.ConcurrentHashMap

class PropertyService : ICarProperty.Stub() {
    private val callbacks = RemoteCallbackList<ICarPropertyEventListener>()
    private val callbackProps = ConcurrentHashMap<IBinder, MutableSet<Int>>()
    private val values = ConcurrentHashMap<Int, Any>()
    private val configs: List<CarPropertyConfig<*>>

    init {
        values.putAll(defaultAtto3Values())
        configs = values.map { (propId, value) -> configFor(propId, value) }
        Log.i(TAG, "init profile=BYD_ATTO3_EXTENDED_2023 props=${configs.size}")
    }

    override fun registerListener(
        propId: Int,
        rate: Float,
        callback: ICarPropertyEventListener?
    ) {
        Log.i(TAG, "registerListener propId=$propId rate=$rate callback=$callback")
        if (callback == null || !values.containsKey(propId)) return
        callbacks.register(callback)
        callbackProps.getOrPut(callback.asBinder()) { mutableSetOf() }.add(propId)
        callback.onEvent(listOf(changeEvent(propId, 0)))
        callback.asBinder().linkToDeath({
            unregisterListener(propId, callback)
        }, 0)
    }

    override fun unregisterListener(propId: Int, callback: ICarPropertyEventListener?) {
        Log.i(TAG, "unregisterListener propId=$propId callback=$callback")
        if (callback == null) return
        val binder = callback.asBinder()
        callbackProps[binder]?.remove(propId)
        if (callbackProps[binder].isNullOrEmpty()) {
            callbackProps.remove(binder)
            callbacks.unregister(callback)
        }
    }

    override fun getPropertyList(): List<CarPropertyConfig<*>> {
        Log.i(TAG, "getPropertyList count=${configs.size}")
        return configs
    }

    override fun getProperty(prop: Int, zone: Int): CarPropertyValue<*>? {
        Log.i(TAG, "getProperty prop=$prop zone=$zone")
        return if (values.containsKey(prop)) currentValue(prop, zone) else null
    }

    override fun setProperty(prop: CarPropertyValue<*>?) {
        Log.i(TAG, "setProperty prop=${prop?.propertyId} area=${prop?.areaId} value=${prop?.value}")
        if (prop == null || prop.value == null) return
        values[prop.propertyId] = prop.value
        fire(prop.propertyId, prop.areaId)
    }

    override fun getReadPermission(propId: Int): String? {
        Log.i(TAG, "getReadPermission propId=$propId")
        return null
    }

    override fun getWritePermission(propId: Int): String? {
        Log.i(TAG, "getWritePermission propId=$propId")
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
            is String -> String::class.java
            is IntArray -> IntArray::class.java
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

    private fun defaultAtto3Values(): Map<Int, Any> = linkedMapOf(
        VehiclePropertyIds.INFO_VIN to "BYDATTO3EXD2023",
        VehiclePropertyIds.INFO_MAKE to "BYD",
        VehiclePropertyIds.INFO_MODEL to "ATTO 3 Extended Range",
        VehiclePropertyIds.INFO_MODEL_YEAR to 2023,
        VehiclePropertyIds.INFO_DRIVER_SEAT to VehicleAreaSeat.SEAT_ROW_1_LEFT,
        VehiclePropertyIds.INFO_EV_BATTERY_CAPACITY to 60_480f,
        VehiclePropertyIds.INFO_EV_CONNECTOR_TYPE to arrayOf(Integer.valueOf(EvConnectorType.UNKNOWN)), // Integer.valueOf(EvConnectorType.COMBO_2) got combo_1 ?
        VehiclePropertyIds.INFO_EV_PORT_LOCATION to PortLocationType.FRONT_RIGHT,
        VehiclePropertyIds.INFO_FUEL_TYPE to arrayOf(Integer.valueOf(FuelType.ELECTRIC)),
        VehiclePropertyIds.INFO_FUEL_CAPACITY to 0f,
        VehiclePropertyIds.EV_BATTERY_LEVEL to 48_384f,
        VehiclePropertyIds.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE to 0f,
        VehiclePropertyIds.EV_BATTERY_DISPLAY_UNITS to VehicleUnit.WATT_HOUR,
        VehiclePropertyIds.EV_CHARGE_PORT_CONNECTED to false,
        VehiclePropertyIds.EV_CHARGE_PORT_OPEN to false,
        VehiclePropertyIds.RANGE_REMAINING to 336f,
        VehiclePropertyIds.DISTANCE_DISPLAY_UNITS to VehicleUnit.KILOMETERS_PER_HOUR,
        VehiclePropertyIds.PERF_VEHICLE_SPEED to 0f,
        VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY to 0f,
        VehiclePropertyIds.PERF_ODOMETER to 12_345f,
        VehiclePropertyIds.GEAR_SELECTION to CarSensorEvent.GEAR_PARK,
        VehiclePropertyIds.CURRENT_GEAR to CarSensorEvent.GEAR_PARK,
        VehiclePropertyIds.IGNITION_STATE to CarSensorEvent.IGNITION_STATE_ON,
        VehiclePropertyIds.PARKING_BRAKE_ON to true,
        VehiclePropertyIds.NIGHT_MODE to false,
        VehiclePropertyIds.ENV_OUTSIDE_TEMPERATURE to 303.15f,
        VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS to VehicleUnit.CELSIUS,
        VehiclePropertyIds.HVAC_TEMPERATURE_SET to 25f,
    )
}