package com.kangrio.carserver.byd.battery

import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import com.kangrio.carserver.byd.BydServices

object BatteryDevice {
    const val DEFAULT_BATTERY_VOLTAGE = 428

    fun getBatteryCapacityWh(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        val batteryCapacityCurrentAmp = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC,
            BYDAutoFeatureIds.STATISTIC_BATTERY_AVAILABLE_POWER
        )

        val capacityWh = (DEFAULT_BATTERY_VOLTAGE * batteryCapacityCurrentAmp)
        return capacityWh
    }

    fun getBatteryLevelPercent(): Float {
//        if (!BydServices.supportAutoService) return Float.MAX_VALUE

        val batteryLevelPercent = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC,
            BYDAutoFeatureIds.STATISTIC_REMAINING_BATTERY_POWER
        )
        return batteryLevelPercent.toFloat() / 10f
    }

    fun getBatteryHealthPercent(): Float {
//        if (!BydServices.supportAutoService) return Float.MAX_VALUE

        val batteryHealthPercent = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC,
            BYDAutoFeatureIds.STATISTIC_BATTERY_HEALTHY_INDEX
        )
        return batteryHealthPercent.toFloat()
    }

    fun getBatteryVoltageVolts(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        return BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_CHARGING,
            BYDAutoFeatureIds.CHARGING_CHARGE_BATTERY_VOLT
        )
    }

    fun getBatteryCurrentAmp(): Float {
//        if (!BydServices.supportAutoService) return Float.MAX_VALUE

        return BydServices.getBydAutoManager().getDouble(
            BYDAutoConstants.BYDAUTO_DEVICE_CHARGING,
            BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT
        )
    }

    fun getBatteryPowerWh(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        val volts = getBatteryVoltageVolts()
        val currentAmp = getBatteryCurrentAmp()

        return (volts.toFloat() * currentAmp).toInt()
    }

    fun getBatteryTempCelsius(): Float {
//        if (!BydServices.supportAutoService) return Float.MAX_VALUE

        val fahrenheit = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC,
            BYDAutoFeatureIds.STATISTIC_HIGHEST_BATTERY_TEMP
        )
        val celsius = (fahrenheit.toFloat() - 32f) * 5f / 9f
        return celsius
    }
}