package com.kangrio.carserver.byd

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.BYDAutoManager
import android.hardware.bydauto.bodywork.BYDAutoBodyworkDevice
import android.hardware.bydauto.charging.BYDAutoChargingDevice
import android.hardware.bydauto.gearbox.BYDAutoGearboxDevice
import android.hardware.bydauto.power.BYDAutoPowerDevice
import android.hardware.bydauto.speed.BYDAutoSpeedDevice
import android.hardware.bydauto.statistic.BYDAutoStatisticDevice
import com.kangrio.carserver.App

@SuppressLint("StaticFieldLeak")
object BydServices {
    private val carContext: Context get() = App.getAppContext()
    val supportAutoService = carContext.getSystemService("auto") != null

    fun getBydAutoManager(): BYDAutoManager {
        return carContext.getSystemService("auto") as BYDAutoManager
    }

    fun getBodyworkDevice(): BYDAutoBodyworkDevice {
        return BYDAutoBodyworkDevice.getInstance(carContext)
    }

    fun getChargingDevice(): BYDAutoChargingDevice {
        return BYDAutoChargingDevice.getInstance(carContext)
    }

    fun getStatisticDevice(): BYDAutoStatisticDevice {
        return BYDAutoStatisticDevice.getInstance(carContext)
    }

    fun getPowerDevice(): BYDAutoPowerDevice {
        return BYDAutoPowerDevice.getInstance(carContext)
    }

    fun getGearboxDevice(): BYDAutoGearboxDevice {
        return BYDAutoGearboxDevice.getInstance(carContext)
    }

    fun getSpeedDevice(): BYDAutoSpeedDevice {
        return BYDAutoSpeedDevice.getInstance(carContext)
    }
}