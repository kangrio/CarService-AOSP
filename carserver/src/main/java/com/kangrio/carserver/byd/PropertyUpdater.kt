package com.kangrio.carserver.byd

import android.car.VehicleAreaType
import android.car.VehicleGear
import android.car.VehicleIgnitionState
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.hardware.BYDAutoManager
import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice
import android.hardware.bydauto.charging.BYDAutoChargingDevice
import android.hardware.bydauto.gearbox.BYDAutoGearboxDevice
import com.kangrio.carserver.App
import com.kangrio.carserver.byd.battery.BatteryDevice
import com.kangrio.carserver.byd.bodywork.BodyworkDevice
import com.kangrio.carserver.byd.charging.ChargingDevice
import com.kangrio.carserver.byd.speed.SpeedDevice
import com.kangrio.carserver.car.service.property.IPropertyUpdater
import com.kangrio.carserver.car.service.property.PropertyService
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.provider.EnergyModelFactory
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.set

class PropertyUpdater(val propertyService: PropertyService) :
    IPropertyUpdater,
    BYDAutoManager.OnBYDAutoListener {

    @Volatile
    private var started = false
    private var bYDAutoManager: BYDAutoManager? = null
    private val simulatorUpdater = SimulatorUpdater(this)

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


    fun start() {
        if (started) return
        started = true
        bYDAutoManager = BYDAutoManager(App.getAppContext())

        collectInitialValue()

        registerListener()

        if (!BydServices.supportAutoService) {
            simulatorUpdater.startSimulate()
        }
    }

    fun release() {
        started = false
        simulatorUpdater.stopSimulate()

        unregisterListener()
    }

    fun collectInitialValue() {
        if (BydServices.supportAutoService) {
            try {
                val capacityWh = BatteryDevice.getBatteryCapacityWh()
                VehicleStore.BATTERY_CAPACITY_WH.set(VehicleStore, capacityWh)

                val batteryLevelPercent = BatteryDevice.getBatteryLevelPercent()
                setBatteryLevelPercent(batteryLevelPercent)

                val batteryHealthPercent = BatteryDevice.getBatteryHealthPercent()
                setBatteryHealthPercent(batteryHealthPercent)

                val speedMeters = SpeedDevice.getSpeedMeterPerSec()
                setSpeedMetersPerSec(speedMeters)

                val consumptionWh = SpeedDevice.getConsumptionWh()
                setConsumptionWhPer100km(consumptionWh)

                val currentGear = BydServices.getGearboxDevice().currentGear
                setCurrentGear(
                    gearMap[currentGear] ?: VehicleStore.CURRENT_GEAR.getOrDefault(
                        VehicleStore, VehicleGear.GEAR_UNKNOWN
                    )
                )

                val powerLevel = BodyworkDevice.getPowerLevel()
                setIgnitionState(
                    ignitionStateMap[powerLevel] ?: VehicleStore.IGNITION_STATE.getOrDefault(
                        VehicleStore, VehicleIgnitionState.UNDEFINED
                    )
                )

                val chargingGunState = ChargingDevice.getChargingPortState()
                setChargingPortConnected(
                    chargingPortConnectedMap[chargingGunState]
                        ?: VehicleStore.CHARGE_PORT_CONNECTED.getOrDefault(
                            VehicleStore, false
                        )
                )
            } catch (e: Throwable) {
                LogUtils.e(TAG, "collect byd initial value failed", e)
            }
        } else {
            val capacityWh = VehicleStore.BATTERY_CAPACITY_WH.getOrDefault(
                VehicleStore,
                DEFAULT_BATTERY_CAPACITY_WH
            )
            VehicleStore.BATTERY_CAPACITY_WH.set(VehicleStore, capacityWh)

            val batteryLevelPercent = VehicleStore.BATTERY_LEVEL_PERCENT.getOrDefault(
                VehicleStore,
                DEFAULT_BATTERY_LEVEL_PERCENT
            )
            setBatteryLevelPercent(batteryLevelPercent)

            val speedMeters = VehicleStore.SPEED_METER_PER_SEC.getOrDefault(VehicleStore, 0f)
            setSpeedMetersPerSec(speedMeters)

            val consumptionWh = VehicleStore.CONSUMPTION_WH_PER_100KM.getOrDefault(
                VehicleStore,
                DEFAULT_CONSUMPTION_WH_PER_100KM
            )
            setConsumptionWhPer100km(consumptionWh)


            val currentGear =
                VehicleStore.CURRENT_GEAR.getOrDefault(VehicleStore, VehicleGear.GEAR_UNKNOWN)
            setCurrentGear(currentGear)

            val ignitionState = VehicleStore.IGNITION_STATE.getOrDefault(
                VehicleStore,
                VehicleIgnitionState.UNDEFINED
            )
            setIgnitionState(ignitionState)

            val chargingGunState =
                VehicleStore.CHARGE_PORT_CONNECTED.getOrDefault(VehicleStore, false)
            setChargingPortConnected(chargingGunState)
        }
        VehicleStore.getAllValue().forEach {
            LogUtils.i(TAG, "collectInitialValue ${it.key} ${it.value}")
        }
    }

    fun registerListener() {
        if (!BydServices.supportAutoService) return
        bYDAutoManager?.registerListener(this)
        bYDAutoManager?.enableDevice(
            BYDAutoConstants.BYDAUTO_DEVICE_BODYWORK, intArrayOf(
                BYDAutoFeatureIds.BODYWORK_POWER_LEVEL
            )
        )
        bYDAutoManager?.enableDevice(
            BYDAutoConstants.BYDAUTO_DEVICE_CHARGING, intArrayOf(
                BYDAutoFeatureIds.CHARGING_GUN_CONNECT_STATE,
                BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT,
                BYDAutoFeatureIds.CHARGING_CHARGE_BATTERY_VOLT
            )
        )
        bYDAutoManager?.enableDevice(
            BYDAutoConstants.BYDAUTO_DEVICE_GEARBOX, intArrayOf(
                BYDAutoFeatureIds.GEARBOX_AUTO_MODE_TYPE,
//                BYDAutoFeatureIds.GEARBOX_PARK_BRAKE_SWITCH
            )
        )
        bYDAutoManager?.enableDevice(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC, intArrayOf(
                BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM,
                BYDAutoFeatureIds.STATISTIC_REMAINING_BATTERY_POWER
            )
        )
        bYDAutoManager?.enableDevice(
            BYDAutoConstants.BYDAUTO_DEVICE_SPEED, intArrayOf(
                BYDAutoFeatureIds.SPEED_AUTO_SPEED
            )
        )
    }

    fun unregisterListener() {
        if (!BydServices.supportAutoService) return
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_BODYWORK)
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_CHARGING)
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_GEARBOX)
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_POWER)
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_SPEED)
        bYDAutoManager?.disableDevice(BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC)
        bYDAutoManager?.unregisterListener(this)
    }

    override fun setSpeedMetersPerSec(speed: Float) {
        setGlobalProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, speed)
        VehicleStore.SPEED_METER_PER_SEC.set(VehicleStore, speed)
    }

    override fun setConsumptionWhPer100km(wh: Int) {
        EnergyModelFactory.setConsumptionWh(wh)
        VehicleStore.CONSUMPTION_WH_PER_100KM.set(VehicleStore, wh)
    }

    override fun setBatteryLevelPercent(percent: Float) {
        val batteryCapacityWh = VehicleStore.BATTERY_CAPACITY_WH
            .getOrDefault(
                VehicleStore,
                DEFAULT_BATTERY_CAPACITY_WH
            )
        val wh = (percent * batteryCapacityWh.toFloat() / 100f).toInt()
        VehicleStore.BATTERY_LEVEL_PERCENT.set(VehicleStore, percent)
        VehicleStore.BATTERY_LEVEL_WH.set(VehicleStore, wh)
        EnergyModelFactory.setBatteryLevelWh(wh)
    }

    fun setBatteryHealthPercent(float: Float) {
        VehicleStore.BATTERY_HEALTH_PERCENT.set(VehicleStore, float)
    }

    override fun setCurrentGear(gear: Int) {
        setParkingBrakeOn(gear == VehicleGear.GEAR_PARK)
        setGlobalProperty(VehiclePropertyIds.GEAR_SELECTION, gear)
        VehicleStore.CURRENT_GEAR.set(VehicleStore, gear)
    }

    override fun setParkingBrakeOn(on: Boolean) {
        setGlobalProperty(VehiclePropertyIds.PARKING_BRAKE_ON, on)
        VehicleStore.PARKING_BRAKE_ON.set(VehicleStore, on)
    }

    override fun setIgnitionState(state: Int) {
        setGlobalProperty(VehiclePropertyIds.IGNITION_STATE, state)
        VehicleStore.IGNITION_STATE.set(VehicleStore, state)
    }

    override fun setChargingPortConnected(connected: Boolean) {
        setGlobalProperty(VehiclePropertyIds.EV_CHARGE_PORT_CONNECTED, connected)
        VehicleStore.CHARGE_PORT_CONNECTED.set(VehicleStore, connected)
    }

    fun setChargingRateWh(volt: Int? = null, amp: Float? = null) {
        volt?.let {
            VehicleStore.BATTERY_VOLTAGE_VOLT.set(VehicleStore, it)
        }
        amp?.let {
            VehicleStore.BATTERY_CURRENT_AMP.set(VehicleStore, it)
        }
        val volts = VehicleStore.BATTERY_VOLTAGE_VOLT.getOrDefault(VehicleStore, 0)
        val currentAmp = VehicleStore.BATTERY_CURRENT_AMP.getOrDefault(VehicleStore, 0f)

        val wh = (volts.toFloat() * currentAmp).toInt()
        VehicleStore.CHARGE_RATE_WH.set(VehicleStore, wh)
    }

    private fun setGlobalProperty(propId: Int, value: Any) {
        propertyService.setProperty(
            CarPropertyValue(
                propId,
                VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, value
            )
        )
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
                setSpeedMetersPerSec(value / 3.6f)
            }

            BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM -> {
                setConsumptionWhPer100km((value * 1_000f).toInt())
            }

            BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT -> {
                setChargingRateWh(amp = value)
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
                setChargingRateWh(volt = value)
            }

            BYDAutoFeatureIds.STATISTIC_REMAINING_BATTERY_POWER -> {
                setBatteryLevelPercent(value.toFloat() / 10)
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

    companion object {
        const val DEFAULT_BATTERY_CAPACITY_WH = 58_636
        const val DEFAULT_BATTERY_LEVEL_PERCENT: Float = 100f
        const val DEFAULT_BATTERY_LEVEL_WH =
            (DEFAULT_BATTERY_CAPACITY_WH.toFloat() * DEFAULT_BATTERY_LEVEL_PERCENT / 100f).toInt()
        const val DEFAULT_CONSUMPTION_WH_PER_100KM = 14_400
        const val DEFAULT_BATTERY_VOLTAGE = 428
        private const val MASS_KG = 2_160
        private const val PEAK_MOTOR_POWER_WH = 150_000
        private const val MAX_DC_CHARGE_WH = 88_000
    }
}
