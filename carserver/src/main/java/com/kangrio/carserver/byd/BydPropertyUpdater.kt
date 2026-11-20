package com.kangrio.carserver.byd

import android.car.VehicleGear
import android.car.VehicleIgnitionState
import android.car.VehicleLightState
import android.hardware.BYDAutoManager
import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice
import android.hardware.bydauto.charging.BYDAutoChargingDevice
import android.hardware.bydauto.gearbox.BYDAutoGearboxDevice
import android.hardware.bydauto.light.BYDAutoLightDevice
import com.kangrio.carserver.App
import com.kangrio.carserver.byd.battery.BatteryDevice
import com.kangrio.carserver.byd.bodywork.BodyworkDevice
import com.kangrio.carserver.byd.charging.ChargingDevice
import com.kangrio.carserver.byd.light.LightDevice
import com.kangrio.carserver.byd.speed.SpeedDevice
import com.kangrio.carserver.car.service.property.BasePropertyUpdater
import com.kangrio.carserver.car.service.property.PropertyService
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class BydPropertyUpdater(propertyService: PropertyService) : BasePropertyUpdater(propertyService),
    BYDAutoManager.OnBYDAutoListener {
    private var bYDAutoManager: BYDAutoManager? = null
    @Volatile
    private var isStarted = false

    val registerListenerMap = mapOf(
        BYDAutoConstants.BYDAUTO_DEVICE_BODYWORK to intArrayOf(
            BYDAutoFeatureIds.BODYWORK_POWER_LEVEL
        ),
        BYDAutoConstants.BYDAUTO_DEVICE_CHARGING to intArrayOf(
            BYDAutoFeatureIds.CHARGING_GUN_CONNECT_STATE,
            BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT,
            BYDAutoFeatureIds.CHARGING_CHARGE_BATTERY_VOLT
        ),
        BYDAutoConstants.BYDAUTO_DEVICE_GEARBOX to intArrayOf(
            BYDAutoFeatureIds.GEARBOX_AUTO_MODE_TYPE,
//                BYDAutoFeatureIds.GEARBOX_PARK_BRAKE_SWITCH
        ),
        BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC to intArrayOf(
            BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM,
            BYDAutoFeatureIds.STATISTIC_REMAINING_BATTERY_POWER
        ),
        BYDAutoConstants.BYDAUTO_DEVICE_SPEED to intArrayOf(
            BYDAutoFeatureIds.SPEED_AUTO_SPEED
        ),
        BYDAutoConstants.BYDAUTO_DEVICE_LIGHT to intArrayOf(
            BYDAutoFeatureIds.LIGHT_LOW_BEAM_LIGHT
        )
    )

    val gearMap = mapOf(
        BYDAutoGearboxDevice.GEAR_P to VehicleGear.GEAR_PARK,
        BYDAutoGearboxDevice.GEAR_R to VehicleGear.GEAR_REVERSE,
        BYDAutoGearboxDevice.GEAR_N to VehicleGear.GEAR_NEUTRAL,
        BYDAutoGearboxDevice.GEAR_D to VehicleGear.GEAR_DRIVE,
    )

    val chargingPortConnectedMap = mapOf(
        BYDAutoChargingDevice.CHARGING_GUN_STATE_CONNECTED_AC_DC to true,
        BYDAutoChargingDevice.CHARGING_GUN_STATE_CONNECTED_AC to true,
        BYDAutoChargingDevice.CHARGING_GUN_STATE_CONNECTED_DC to true,
        BYDAutoChargingDevice.CHARGING_GUN_STATE_CONNECTED_NONE to false,
    )

    val ignitionStateMap = mapOf(
        BYDAutoBodyworkDevice.BODYWORK_POWER_LEVEL_OFF to VehicleIgnitionState.OFF,
        BYDAutoBodyworkDevice.BODYWORK_POWER_LEVEL_ON to VehicleIgnitionState.ON,
        BYDAutoBodyworkDevice.BODYWORK_POWER_LEVEL_ACC to VehicleIgnitionState.ACC,
        BYDAutoBodyworkDevice.BODYWORK_POWER_LEVEL_OK to VehicleIgnitionState.START,
        BYDAutoBodyworkDevice.BODYWORK_POWER_LEVEL_INVALID to VehicleIgnitionState.UNDEFINED,
    )

    val headLightStateMap = mapOf(
        BYDAutoLightDevice.LIGHT_ON to VehicleLightState.ON,
        BYDAutoLightDevice.LIGHT_OFF to VehicleLightState.OFF
    )

    override fun start() {
        if (isStarted) return

        isStarted = true
        setupInitialProperties()
        registerListener()
    }

    override fun release() {
        isStarted = false

        unregisterListener()
    }

    override fun setupInitialProperties() {
        bYDAutoManager = BYDAutoManager(App.getAppContext())

        if (BydServices.supportAutoService) {
            try {
                val lowBeamStateState = LightDevice.getLowBeamState()
                val lowBeamLightState = headLightStateMap[lowBeamStateState] ?: VehicleLightState.OFF
                setLowBeamLightState(lowBeamLightState)

                val driverSeat = VehicleStore.getValue(VehicleStore.DRIVER_SEAT, BodyworkDevice.getDriverSeat())
                setDriverSeat(driverSeat)

                val capacityWh = VehicleStore.getValue(VehicleStore.BATTERY_CAPACITY_WH, BatteryDevice.getBatteryCapacityWh())
                setBatteryCapacityWatts(capacityWh)

                val batteryLevelPercent = BatteryDevice.getBatteryLevelPercent()
                setBatteryLevelPercent(batteryLevelPercent)

                val batteryHealthPercent = BatteryDevice.getBatteryHealthPercent()
                setBatteryHealthPercent(batteryHealthPercent)

                val speedMeters = SpeedDevice.getSpeedMeterPerSec()
                setSpeedMetersPerSec(speedMeters)

                val consumptionWh = SpeedDevice.getConsumptionWh()
                setConsumptionKmPerKwh(100_000f / consumptionWh.coerceAtLeast(1))

                val currentGear = BydServices.getGearboxDevice().currentGear
                val gear = gearMap[currentGear] ?: VehicleStore.getValue(
                    VehicleStore.CURRENT_GEAR, VehicleGear.GEAR_UNKNOWN
                )
                setCurrentGear(gear)

                val powerLevel = BodyworkDevice.getPowerLevel()
                val ignitionState  = ignitionStateMap[powerLevel] ?: VehicleStore.getValue(
                    VehicleStore.IGNITION_STATE, VehicleIgnitionState.UNDEFINED
                )
                setIgnitionState(ignitionState)

                val chargingGunState = ChargingDevice.getChargingPortState()
                val chargingPortConnected = chargingPortConnectedMap[chargingGunState]
                    ?: VehicleStore.getValue(
                        VehicleStore.CHARGE_PORT_CONNECTED, false
                    )
                setChargingPortConnected(chargingPortConnected)
            } catch (e: Throwable) {
                LogUtils.e(TAG, "collect byd initial value failed", e)
            }
        }

        VehicleStore.getAllValue().forEach {
            LogUtils.i(TAG, "collectInitialValue ${it.key} ${it.value}")
        }
    }

    fun registerListener() {
        if (!BydServices.supportAutoService) return
        unregisterListener()
        bYDAutoManager?.registerListener(this)
        registerListenerMap.forEach { (deviceType, eventTypes) ->
            bYDAutoManager?.enableDevice(deviceType, eventTypes)
        }
    }

    fun unregisterListener() {
        if (!BydServices.supportAutoService) return
        registerListenerMap.keys.forEach { deviceType ->
            bYDAutoManager?.disableDevice(deviceType)
        }
        bYDAutoManager?.unregisterListener(this)
    }

    override fun onChanged(device_type: Int, event_type: Int, value: Float, data: Any?) {
        LogUtils.i(TAG, "onChanged device_type=$device_type event_type=$event_type value=$value")
        handleFloatEvent(event_type, value)
    }

    override fun onChanged(device_type: Int, event_type: Int, value: Int, data: Any?) {
        LogUtils.i(TAG, "onChanged device_type=$device_type event_type=$event_type value=$value")
        handleIntEvent(event_type, value)
    }

    private fun handleFloatEvent(eventType: Int, value: Float) {
        when (eventType) {
            BYDAutoFeatureIds.SPEED_AUTO_SPEED -> {
                // km/h to m/s
                setSpeedMetersPerSec(value / 3.6f)
            }

            BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM -> {
                // 100km/kwh to 1kwh/100km
                setConsumptionKmPerKwh(100_000f / (value * 1_000f).coerceAtLeast(0f))
            }

            BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT -> {
                // Amp to milliAmp
                setChargingRateMilliWatts(mAh = value * 1_000 * -1)
            }
        }
    }

    private fun handleIntEvent(eventType: Int, value: Int) {
        when (eventType) {
            BYDAutoFeatureIds.BODYWORK_POWER_LEVEL -> {
                setIgnitionState(ignitionStateMap[value] ?: VehicleIgnitionState.UNDEFINED)
            }

            BYDAutoFeatureIds.CHARGING_GUN_CONNECT_STATE -> {
                setChargingPortConnected(chargingPortConnectedMap[value] ?: false)
            }

            BYDAutoFeatureIds.GEARBOX_AUTO_MODE_TYPE -> {
                BydServices.getGearboxDevice().currentGear.let {
                    setCurrentGear(gearMap[it] ?: VehicleGear.GEAR_UNKNOWN)
                }
            }

            BYDAutoFeatureIds.CHARGING_CHARGE_BATTERY_VOLT -> {
                setChargingRateMilliWatts(volt = value)
            }

            BYDAutoFeatureIds.STATISTIC_REMAINING_BATTERY_POWER -> {
                setBatteryLevelPercent(value.toFloat() / 10)
            }

            BYDAutoFeatureIds.LIGHT_LOW_BEAM_LIGHT -> {
                val lowBeamLightState = headLightStateMap[value] ?: VehicleLightState.OFF
                setLowBeamLightState(lowBeamLightState)
            }
        }
    }

    override fun onChanged(
        device_type: Int,
        event_type: Int,
        value: ByteArray?,
        data: Any?
    ) {
    }

    override fun onError(errCode: Int, errMessage: String?) {
    }
}
