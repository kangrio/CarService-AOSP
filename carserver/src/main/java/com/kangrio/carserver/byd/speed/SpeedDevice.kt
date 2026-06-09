package com.kangrio.carserver.byd.speed

import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import com.kangrio.carserver.byd.BydServices

object SpeedDevice {
    fun getSpeedMeterPerSec(): Float {
//        if (!BydServices.supportAutoService) return Float.MAX_VALUE

        val speedKmh = BydServices.getBydAutoManager().getDouble(
            BYDAutoConstants.BYDAUTO_DEVICE_SPEED,
            BYDAutoFeatureIds.SPEED_AUTO_SPEED
        )

        return speedKmh / 3.6f
    }

    fun getConsumptionWh(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        return (BydServices.getBydAutoManager().getDouble(
            BYDAutoConstants.BYDAUTO_DEVICE_STATISTIC,
            BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM
        ) * 1000f).toInt()
    }
}