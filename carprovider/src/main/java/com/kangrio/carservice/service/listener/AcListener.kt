package com.kangrio.carservice.service.listener

import android.hardware.bydauto.BYDAutoEventValue
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.ac.AbsBYDAutoAcListener
import android.preference.PreferenceManager
import com.kangrio.carservice.App
import com.kangrio.carservice.Constant

class AcListener : AbsBYDAutoAcListener() {
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())

    private var hvacOn: Boolean = prefs.getBoolean(Constant.PREFS_HVAC_ON, false)

    override fun onDataEventChanged(eventType: Int, eventValue: BYDAutoEventValue?) {
        when (eventType) {
            BYDAutoFeatureIds.AC_ONLINE_STATE -> {
//                hvacOn = eventValue?.intValue == BYDAutoAcDevice.AC_ONLINE_STATE_ON
//                prefs.edit { putBoolean(Constant.PREFS_HVAC_ON, hvacOn) }
            }
        }
    }
}