package com.kangrio.carserver.car.service.property

import android.car.VehicleAreaType
import android.car.VehicleGear
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.provider.EnergyModelFactory
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

abstract class BasePropertyUpdater(val propertyService: PropertyService) : IPropertyUpdater {
    final override fun setGlobalProperty(propId: Int, value: Any) {
        propertyService.setProperty(
            CarPropertyValue(
                propId,
                VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL, value
            )
        )
    }

    final override fun setBatteryCapacityWatts(watts: Int) {
        LogUtils.i(TAG, "setBatteryCapacityWatts watts=$watts")
        setGlobalProperty(VehiclePropertyIds.INFO_EV_BATTERY_CAPACITY, watts)
        VehicleStore.setValue(VehicleStore.BATTERY_CAPACITY_WH, watts)
    }

    final override fun setSpeedMetersPerSec(speed: Float) {
        LogUtils.i(TAG, "setSpeedMetersPerSec speed=${speed}m/s, ${speed * 3.6}km/h")
        setGlobalProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, speed)
        VehicleStore.setValue(VehicleStore.SPEED_METER_PER_SEC, speed)
    }

    private fun setConsumptionWhPer100km(watts: Int) {
        LogUtils.i(TAG, "setConsumptionWhPer100km watts=$watts")
        EnergyModelFactory.setConsumptionWh(watts)
    }

    final override fun setConsumptionKmPerKwh(km: Float) {
        LogUtils.i(TAG, "setConsumptionKmPerKwh km=$km")
        setGlobalProperty(291504658, km)
        VehicleStore.setValue(VehicleStore.CONSUMPTION_KM_PER_KWH, km)
        setConsumptionWhPer100km((100_000f / km.coerceAtLeast(1f)).toInt())
    }

    final override fun setBatteryLevelPercent(percent: Float) {
        LogUtils.i(TAG, "setBatteryLevelPercent percent=$percent")
        val batteryCapacityWh = VehicleStore.getValue(
            VehicleStore.BATTERY_CAPACITY_WH,
            DEFAULT_BATTERY_CAPACITY_WH
        )
        val wh = (percent * batteryCapacityWh.toFloat() / 100f).toInt()
        setGlobalProperty(VehiclePropertyIds.EV_BATTERY_LEVEL, wh.toFloat())
        VehicleStore.setValue(VehicleStore.BATTERY_LEVEL_PERCENT, percent)
        VehicleStore.setValue(VehicleStore.BATTERY_LEVEL_WH, wh)
        EnergyModelFactory.setBatteryLevelWh(wh)
    }

    final override fun setBatteryHealthPercent(percent: Float) {
        LogUtils.i(TAG, "setBatteryHealthPercent percent=$percent")
        VehicleStore.setValue(VehicleStore.BATTERY_HEALTH_PERCENT, percent)
    }

    final override fun setCurrentGear(gear: Int) {
        LogUtils.i(TAG, "setCurrentGear gear=$gear")
        setParkingBrakeOn(gear == VehicleGear.GEAR_PARK)
        setGlobalProperty(VehiclePropertyIds.GEAR_SELECTION, gear)
        VehicleStore.setValue(VehicleStore.CURRENT_GEAR, gear)
    }

    final override fun setParkingBrakeOn(on: Boolean) {
        LogUtils.i(TAG, "setParkingBrakeOn on=$on")
        setGlobalProperty(VehiclePropertyIds.PARKING_BRAKE_ON, on)
        VehicleStore.setValue(VehicleStore.PARKING_BRAKE_ON, on)
    }

    final override fun setIgnitionState(state: Int) {
        LogUtils.i(TAG, "setIgnitionState state=$state")
        setGlobalProperty(VehiclePropertyIds.IGNITION_STATE, state)
        VehicleStore.setValue(VehicleStore.IGNITION_STATE, state)
    }

    final override fun setChargingPortConnected(connected: Boolean) {
        LogUtils.i(TAG, "setChargingPortConnected connected=$connected")
        setGlobalProperty(VehiclePropertyIds.EV_CHARGE_PORT_CONNECTED, connected)
        VehicleStore.setValue(VehicleStore.CHARGE_PORT_CONNECTED, connected)
    }

    final override fun setDriverSeat(seat: Int) {
        LogUtils.i(TAG, "setRightDriver rightDriver=$seat")
        setGlobalProperty(VehiclePropertyIds.INFO_DRIVER_SEAT, seat)
        VehicleStore.setValue(VehicleStore.DRIVER_SEAT, seat)
    }

    override fun setLowBeamLightState(state: Int) {
        LogUtils.i(TAG, "setLowBeamLightState state=$state")
        setGlobalProperty(VehiclePropertyIds.HEADLIGHTS_STATE, state)
        VehicleStore.setValue(VehicleStore.VEHICLE_LOW_BEAM_STATE, state)
    }

    /**
     * EV instantaneous charge rate in milliwatts.
     * Positive rate indicates battery is being charged, and Negative rate indicates battery being discharged.
     */
    final override fun setChargingRateMilliWatts(volt: Int?, mAh: Float?) {
        volt?.let {
            VehicleStore.setValue(VehicleStore.BATTERY_VOLTAGE_VOLT, it)
        }
        mAh?.let {
            VehicleStore.setValue(VehicleStore.BATTERY_CURRENT_MAH, it)
        }
        val volts = VehicleStore.getValue(VehicleStore.BATTERY_VOLTAGE_VOLT, 0)
        val mAh = VehicleStore.getValue(VehicleStore.BATTERY_CURRENT_MAH, 0f)

        val milliwatts = (volts.toFloat() * mAh)
        LogUtils.i(TAG, "setChargingRateWh volt=$volts mAh=$mAh milliwatts=${milliwatts.toBigDecimal().unscaledValue()}")
        setGlobalProperty(VehiclePropertyIds.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE, milliwatts)
        VehicleStore.setValue(VehicleStore.CHARGE_RATE_MWH, milliwatts)
    }

    companion object {
        const val DEFAULT_BATTERY_CAPACITY_WH = 58_636
        const val DEFAULT_BATTERY_LEVEL_PERCENT: Float = 100f
        const val DEFAULT_BATTERY_LEVEL_WH =
            (DEFAULT_BATTERY_CAPACITY_WH.toFloat() * DEFAULT_BATTERY_LEVEL_PERCENT / 100f).toInt()
        const val DEFAULT_CONSUMPTION_KM_PER_KWH = 100_000f / 14_400f
        const val DEFAULT_BATTERY_VOLTAGE = 428
    }
}