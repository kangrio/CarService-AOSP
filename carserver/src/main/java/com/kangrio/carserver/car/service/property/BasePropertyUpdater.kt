package com.kangrio.carserver.car.service.property

import android.car.VehicleAreaType
import android.car.VehicleGear
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import androidx.annotation.CallSuper
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

    final override fun setBatteryCapacityWh(wh: Int) {
        LogUtils.i(TAG, "setBatteryCapacityWh wh=$wh")
        setGlobalProperty(VehiclePropertyIds.INFO_EV_BATTERY_CAPACITY, wh)
        VehicleStore.setValue(VehicleStore.BATTERY_CAPACITY_WH, wh)
    }

    final override fun setSpeedMetersPerSec(speed: Float) {
        LogUtils.i(TAG, "setSpeedMetersPerSec speed=${speed}m/s, ${speed * 3.6}km/h")
        setGlobalProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, speed)
        VehicleStore.setValue(VehicleStore.SPEED_METER_PER_SEC, speed)
    }

    final override fun setConsumptionWhPer100km(wh: Int) {
        LogUtils.i(TAG, "setConsumptionWhPer100km wh=$wh")
        EnergyModelFactory.setConsumptionWh(wh)
        VehicleStore.setValue(VehicleStore.CONSUMPTION_WH_PER_100KM, wh)
    }

    final override fun setBatteryLevelPercent(percent: Float) {
        LogUtils.i(TAG, "setBatteryLevelPercent percent=$percent")
        val batteryCapacityWh = VehicleStore.getValue(
            VehicleStore.BATTERY_CAPACITY_WH,
            DEFAULT_BATTERY_CAPACITY_WH
        )
        val wh = (percent * batteryCapacityWh.toFloat() / 100f).toInt()
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

    final override fun setLowBeamLightOn(on: Boolean) {
        LogUtils.i(TAG, "setLowBeamLightOn on=$on")
        VehicleStore.setValue(VehicleStore.VEHICLE_LOW_BEAM_STATE_ON, on)
    }

    final override fun setChargingRateWh(volt: Int?, amp: Float?) {
        volt?.let {
            VehicleStore.setValue(VehicleStore.BATTERY_VOLTAGE_VOLT, it)
        }
        amp?.let {
            VehicleStore.setValue(VehicleStore.BATTERY_CURRENT_AMP, it)
        }
        val volts = VehicleStore.getValue(VehicleStore.BATTERY_VOLTAGE_VOLT, 0)
        val currentAmp = VehicleStore.getValue(VehicleStore.BATTERY_CURRENT_AMP, 0f)

        val wh = (volts.toFloat() * currentAmp).toInt()
        LogUtils.i(TAG, "setChargingRateWh volt=$volts currentAmp=$currentAmp wh=$wh")
        VehicleStore.setValue(VehicleStore.CHARGE_RATE_WH, wh)
    }

    companion object {
        const val DEFAULT_BATTERY_CAPACITY_WH = 58_636
        const val DEFAULT_BATTERY_LEVEL_PERCENT: Float = 100f
        const val DEFAULT_BATTERY_LEVEL_WH =
            (DEFAULT_BATTERY_CAPACITY_WH.toFloat() * DEFAULT_BATTERY_LEVEL_PERCENT / 100f).toInt()
        const val DEFAULT_CONSUMPTION_WH_PER_100KM = 14_400
        const val DEFAULT_BATTERY_VOLTAGE = 428
    }
}