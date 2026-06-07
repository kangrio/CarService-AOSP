package com.kangrio.carservice.service.listener

import android.hardware.bydauto.BYDAutoEventValue
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.charging.AbsBYDAutoChargingListener
import android.preference.PreferenceManager
import com.kangrio.carservice.App
import com.kangrio.carservice.Constant

class ChargingListener : AbsBYDAutoChargingListener() {
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())

    private var isChargingPortConnected: Boolean =
        prefs.getBoolean(Constant.PREFS_CHARGING_PORT_CONNECTED, false)
    private var isCharging: Boolean = prefs.getBoolean(Constant.PREFS_CHARGING_STATE, false)
    private var batteryCurrent: Float = 0.0f
    private var batteryVoltage: Int = 0
    private var batteryPowerWatts: Int = prefs.getInt(Constant.PREFS_CHARGING_POWER_WATTS, 0)

    override fun onDataEventChanged(eventType: Int, eventValue: BYDAutoEventValue?) {
        when (eventType) {
            BYDAutoFeatureIds.CHARGING_GUN_CONNECT_STATE -> {
//                isChargingPortConnected = eventValue?.intValue == BYDAutoChargingDevice.CHARGING_GUN_STATE_CONNECTED_AC_DC
//                prefs.edit { putBoolean(Constant.PREFS_CHARGING_PORT_CONNECTED, isChargingPortConnected) }
            }

            BYDAutoFeatureIds.CHARGING_BATTERRY_DEVICE_STATE -> {
//                isCharging = eventValue?.intValue == BYDAutoChargingDevice.CHARGING_BATTERY_STATE_CHARGING
//                prefs.edit { putBoolean(Constant.PREFS_CHARGING_STATE, isCharging) }
            }

            BYDAutoFeatureIds.CHARGING_CHARGE_CURRENT -> {
//                batteryCurrent = eventValue?.doubleValue?.toFloat() ?: 0.0f
//                batteryPowerWatts = (batteryCurrent * batteryVoltage).toInt()
//                prefs.edit { putInt(Constant.PREFS_CHARGING_POWER_WATTS, batteryPowerWatts) }
            }

            BYDAutoFeatureIds.CHARGING_CHARGE_BATTERY_VOLT -> {
//                batteryVoltage = eventValue?.intValue ?: 0
//                batteryPowerWatts = (batteryCurrent * batteryVoltage).toInt()
//                prefs.edit { putInt(Constant.PREFS_CHARGING_POWER_WATTS, batteryPowerWatts) }
            }
        }
    }
}