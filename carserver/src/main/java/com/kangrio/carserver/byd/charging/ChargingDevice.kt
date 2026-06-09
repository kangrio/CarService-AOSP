package com.kangrio.carserver.byd.charging

import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import com.kangrio.carserver.byd.BydServices
import com.kangrio.carserver.byd.battery.BatteryDevice

object ChargingDevice {
    fun getChargingRateWh(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        return BatteryDevice.getBatteryPowerWh()
    }

    fun getChargingPortState(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        val gunState = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_CHARGING,
            BYDAutoFeatureIds.CHARGING_GUN_CONNECT_STATE
        )
        return gunState
    }
}