package com.kangrio.carserver.service

import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsConfiguration
import android.car.drivingstate.CarUxRestrictionsManager
import android.car.drivingstate.ICarUxRestrictionsChangeListener
import android.car.drivingstate.ICarUxRestrictionsManager
import android.os.RemoteCallbackList
import android.util.Log
import com.kangrio.carserver.TAG

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
        Log.i(TAG, "getCurrentUxRestrictions displayId=$displayId")
        return currentRestrictions
    }

    override fun registerUxRestrictionsChangeListener(
        listener: ICarUxRestrictionsChangeListener?,
        displayId: Int
    ) {
        Log.i(TAG, "registerUxRestrictionsChangeListener displayId=$displayId listener=$listener")

        if (listener == null) return

        listeners.register(listener)

        try {
            listener.onUxRestrictionsChanged(currentRestrictions)
        } catch (e: Throwable) {
            Log.w(TAG, "send initial ux restriction failed", e)
        }
    }

    override fun unregisterUxRestrictionsChangeListener(
        listener: ICarUxRestrictionsChangeListener?
    ) {
        Log.i(TAG, "unregisterUxRestrictionsChangeListener listener=$listener")

        if (listener != null) {
            listeners.unregister(listener)
        }
    }

    override fun getRestrictionMode(): String {
        Log.i(TAG, "getRestrictionMode")
        return restrictionMode
    }

    override fun setRestrictionMode(mode: String?): Boolean {
        Log.i(TAG, "setRestrictionMode mode=$mode")

        restrictionMode = mode ?: CarUxRestrictionsManager.UX_RESTRICTION_MODE_BASELINE

        notifyUxRestrictionsChanged()
        return true
    }

    override fun saveUxRestrictionsConfigurationForNextBoot(
        configs: List<CarUxRestrictionsConfiguration?>?
    ): Boolean {
        Log.i(TAG, "saveUxRestrictionsConfigurationForNextBoot count=${configs?.size ?: 0}")
        return true
    }

    override fun getStagedConfigs(): List<CarUxRestrictionsConfiguration?> {
        Log.i(TAG, "getStagedConfigs")
        return emptyList()
    }

    override fun getConfigs(): List<CarUxRestrictionsConfiguration?> {
        Log.i(TAG, "getConfigs")
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
                    Log.w(TAG, "notify listener failed", e)
                }
            }
        } finally {
            listeners.finishBroadcast()
        }
    }
}