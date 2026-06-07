package com.kangrio.carservice.service

import android.hardware.bydauto.charging.BYDAutoChargingDevice
import android.hardware.bydauto.statistic.BYDAutoStatisticDevice
import com.kangrio.carservice.App

object BydServices {
    private val carContext: CarContext = CarContext(App.getAppContext())
    private var supportAutoService = isSupportBydAutoService()

    private fun isSupportBydAutoService(): Boolean {
        return carContext.getSystemService("auto") != null
    }

    fun getChargingDevice(): BYDAutoChargingDevice? {
        if (!supportAutoService) return null
        return BYDAutoChargingDevice.getInstance(carContext)
    }

    fun getStatisticDevice(): BYDAutoStatisticDevice? {
        if (!supportAutoService) return null
        return BYDAutoStatisticDevice.getInstance(carContext)
    }
}