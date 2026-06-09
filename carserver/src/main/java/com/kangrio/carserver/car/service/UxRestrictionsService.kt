package com.kangrio.carserver.car.service

import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsConfiguration
import android.car.drivingstate.CarUxRestrictionsManager
import android.car.drivingstate.ICarUxRestrictionsChangeListener
import android.car.drivingstate.ICarUxRestrictionsManager
import android.os.RemoteCallbackList
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class UxRestrictionsService : ICarUxRestrictionsManager.Stub() {

    private val listeners = RemoteCallbackList<ICarUxRestrictionsChangeListener>()

    @Volatile
    private var restrictionMode: String = CarUxRestrictionsManager.UX_RESTRICTION_MODE_BASELINE

    @Volatile
    private var currentRestrictions: CarUxRestrictions =
        CarUxRestrictions.Builder(
            false, // requiresDistractionOptimization
            0,     // no restrictions
            System.currentTimeMillis()
        ).build()

    override fun getCurrentUxRestrictions(displayId: Int): CarUxRestrictions {
        LogUtils.i(TAG, "getCurrentUxRestrictions displayId=$displayId")
        return currentRestrictions
    }

    override fun registerUxRestrictionsChangeListener(
        listener: ICarUxRestrictionsChangeListener?,
        displayId: Int
    ) {
        LogUtils.i(TAG, "registerUxRestrictionsChangeListener displayId=$displayId listener=$listener")

        if (listener == null) return

        listeners.register(listener)

        try {
            listener.onUxRestrictionsChanged(currentRestrictions)
        } catch (e: Throwable) {
            LogUtils.w(TAG, "send initial ux restriction failed", e)
        }
    }

    override fun unregisterUxRestrictionsChangeListener(
        listener: ICarUxRestrictionsChangeListener?
    ) {
        LogUtils.i(TAG, "unregisterUxRestrictionsChangeListener listener=$listener")

        if (listener != null) {
            listeners.unregister(listener)
        }
    }

    override fun getRestrictionMode(): String {
        LogUtils.i(TAG, "getRestrictionMode")
        return restrictionMode
    }

    override fun setRestrictionMode(mode: String?): Boolean {
        LogUtils.i(TAG, "setRestrictionMode mode=$mode")

        restrictionMode = mode ?: CarUxRestrictionsManager.UX_RESTRICTION_MODE_BASELINE

        notifyUxRestrictionsChanged()
        return true
    }

    override fun saveUxRestrictionsConfigurationForNextBoot(
        configs: List<CarUxRestrictionsConfiguration?>?
    ): Boolean {
        LogUtils.i(TAG, "saveUxRestrictionsConfigurationForNextBoot count=${configs?.size ?: 0}")
        return true
    }

    override fun getStagedConfigs(): List<CarUxRestrictionsConfiguration?> {
        LogUtils.i(TAG, "getStagedConfigs")
        return emptyList()
    }

    override fun getConfigs(): List<CarUxRestrictionsConfiguration?> {
        LogUtils.i(TAG, "getConfigs")
        return emptyList()
    }

    private fun notifyUxRestrictionsChanged() {
        val count = listeners.beginBroadcast()
        try {
            for (i in 0 until count) {
                try {
                    listeners.getBroadcastItem(i)
                        ?.onUxRestrictionsChanged(currentRestrictions)
                } catch (e: Throwable) {
                    LogUtils.w(TAG, "notify listener failed", e)
                }
            }
        } finally {
            listeners.finishBroadcast()
        }
    }
}