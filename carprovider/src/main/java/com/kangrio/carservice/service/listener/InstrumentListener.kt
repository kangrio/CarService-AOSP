package com.kangrio.carservice.service.listener

import android.hardware.bydauto.BYDAutoEventValue
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.instrument.AbsBYDAutoInstrumentListener
import android.preference.PreferenceManager
import com.kangrio.carservice.App
import com.kangrio.carservice.Constant

class InstrumentListener : AbsBYDAutoInstrumentListener() {
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())

    private var externalTempCelsius: Int = prefs.getInt(Constant.PREFS_EXTERNAL_TEMP_CELSIUS, 0)


    override fun onDataEventChanged(eventType: Int, eventValue: BYDAutoEventValue?) {
        when (eventType) {
            BYDAutoFeatureIds.INSTRUMENT_DD_OUT_TEMP -> {
//                externalTempCelsius = eventValue?.intValue ?: 0
//                externalTempCelsius -= 40
//                prefs.edit { putInt(Constant.PREFS_EXTERNAL_TEMP_CELSIUS, externalTempCelsius) }
            }
        }
    }
}