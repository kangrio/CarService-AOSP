package com.kangrio.carservice.service.listener

import android.hardware.bydauto.BYDAutoEventValue
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.statistic.AbsBYDAutoStatisticListener
import android.preference.PreferenceManager
import com.kangrio.carservice.App
import com.kangrio.carservice.Constant
import com.kangrio.carservice.provider.EnergyModelFactory

class StatisticListener : AbsBYDAutoStatisticListener() {
    private var nextUpdateTime = 0L
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
    private var batteryPercent: Int = prefs.getInt(Constant.PREFS_BATTERY_PERCENT, 0)
    private var lastElectricConsumptionWatts =
        prefs.getInt(Constant.PREFS_ELECTRIC_CONSUMPTION_WATTS, 0)
    private var batteryTempCelsius: Int = prefs.getInt(Constant.PREFS_BATTERY_TEMP_CELSIUS, 0)
    private var maximumChargingRateWatts: Int =
        prefs.getInt(Constant.PREFS_MAXIMUM_CHARGING_RATE_WATTS, 0)

    override fun onDataEventChanged(eventType: Int, eventValue: BYDAutoEventValue?) {
        when (eventType) {
            BYDAutoFeatureIds.STATISTIC_SOC_BATTERY_PERCENTAGE -> {
                batteryPercent = eventValue?.intValue ?: 0
//                prefs.edit { putInt(Constant.PREFS_BATTERY_PERCENT, batteryPercent) }
                EnergyModelFactory.setBatteryPercentage(batteryPercent)
            }

            BYDAutoFeatureIds.STATISTIC_LAST_ELEC_CON_PHM -> {
                lastElectricConsumptionWatts = ((eventValue?.doubleValue ?: 0.0) * 1_000).toInt()
//                prefs.edit { putInt(Constant.PREFS_ELECTRIC_CONSUMPTION_WATTS, lastElectricConsumptionWatts) }

                val now = System.currentTimeMillis()
                if (now >= nextUpdateTime) {
                    EnergyModelFactory.setConsumption(lastElectricConsumptionWatts)
                    nextUpdateTime = now + THRESHOLD
                }
            }

            BYDAutoFeatureIds.STATISTIC_HIGHEST_BATTERY_TEMP -> {
//                batteryTempCelsius = eventValue?.intValue ?: 0
//                batteryTempCelsius -= 40
//                prefs.edit { putInt(Constant.PREFS_BATTERY_TEMP_CELSIUS, batteryTempCelsius) }
            }

            BYDAutoFeatureIds.STATISTIC_MAX_CHARGE_POWER_ALLOW -> {
//                maximumChargingRateWatts = eventValue?.intValue ?: 0
//                prefs.edit { putInt(Constant.PREFS_MAXIMUM_CHARGING_RATE_WATTS, maximumChargingRateWatts) }
            }
        }
    }

    companion object {
        private const val THRESHOLD = 10_000L
    }
}