package com.kangrio.carserver.car.service

import android.car.hardware.power.CarPowerPolicy
import android.car.hardware.power.CarPowerPolicyFilter
import android.car.hardware.power.ICarPower
import android.car.hardware.power.ICarPowerPolicyListener
import android.car.hardware.power.ICarPowerStateListener
import android.car.hardware.power.PowerComponent
import android.car.hardware.power.PowerComponentUtil
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import java.util.UUID

class PowerService : ICarPower.Stub() {
    val enabledComponents = intArrayOf(
        PowerComponent.AUDIO,
        PowerComponent.BLUETOOTH,
        PowerComponent.CELLULAR,
        PowerComponent.CPU,
        PowerComponent.DISPLAY,
        PowerComponent.ETHERNET,
        PowerComponent.INPUT,
        PowerComponent.LOCATION,
        PowerComponent.MEDIA,
        PowerComponent.MICROPHONE,
        PowerComponent.NFC,
        PowerComponent.PROJECTION,
        PowerComponent.TRUSTED_DEVICE_DETECTION,
        PowerComponent.VISUAL_INTERACTION,
        PowerComponent.VOICE_INTERACTION,
        PowerComponent.WIFI
    )

    val disabledComponents = intArrayOf()

    val powerPolicy = CarPowerPolicy(
        UUID.randomUUID().toString().substring(16),
        enabledComponents,
        disabledComponents
    )

    override fun addPowerPolicyListener(p0: CarPowerPolicyFilter?, p1: ICarPowerPolicyListener?) {
        LogUtils.i(TAG, "addPowerPolicyListener p0=$p0 p1=$p1")
    }

    override fun applyPowerPolicy(p0: String?) {
        LogUtils.i(TAG, "applyPowerPolicy p0=$p0")
    }

    override fun finished(p0: ICarPowerStateListener?) {
        LogUtils.i(TAG, "finished p0=$p0")
    }

    override fun getCurrentPowerPolicy(): CarPowerPolicy {
        LogUtils.i(TAG, "getCurrentPowerPolicy")
        return powerPolicy
    }

    override fun getPowerState(): Int {
        LogUtils.i(TAG, "getPowerState")
        return ON
    }

    override fun registerListener(p0: ICarPowerStateListener?) {
        LogUtils.i(TAG, "registerListener p0=$p0")
    }

    override fun registerListenerWithCompletion(p0: ICarPowerStateListener?) {
        LogUtils.i(TAG, "registerListenerWithCompletion p0=$p0")
    }

    override fun removePowerPolicyListener(p0: ICarPowerPolicyListener?) {
        LogUtils.i(TAG, "removePowerPolicyListener p0=$p0")
    }

    override fun requestShutdownOnNextSuspend() {
        LogUtils.i(TAG, "requestShutdownOnNextSuspend")
    }

    override fun scheduleNextWakeupTime(p0: Int) {
        LogUtils.i(TAG, "scheduleNextWakeupTime p0=$p0")
    }

    override fun setPowerPolicyGroup(p0: String?) {
        LogUtils.i(TAG, "setPowerPolicyGroup p0=$p0")
    }

    override fun unregisterListener(p0: ICarPowerStateListener?) {
        LogUtils.i(TAG, "unregisterListener p0=$p0")
    }

    companion object {
        const val INVALID: Int = 0
        const val ON: Int = 6
        const val SHUTDOWN_CANCELLED: Int = 8
        const val SHUTDOWN_ENTER: Int = 5
        const val SHUTDOWN_PREPARE: Int = 7
        const val SUSPEND_ENTER: Int = 2
        const val SUSPEND_EXIT: Int = 3
        const val WAIT_FOR_VHAL: Int = 1
    }
}