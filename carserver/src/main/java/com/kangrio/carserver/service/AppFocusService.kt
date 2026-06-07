package com.kangrio.carserver.service

import android.car.CarAppFocusManager.APP_FOCUS_REQUEST_FAILED
import android.car.CarAppFocusManager.APP_FOCUS_REQUEST_SUCCEEDED
import android.car.IAppFocus
import android.car.IAppFocusListener
import android.car.IAppFocusOwnershipCallback
import android.os.RemoteCallbackList
import android.util.Log

class AppFocusService : IAppFocus.Stub() {
    private val TAG = "AppFocusService"
    private val listeners = RemoteCallbackList<IAppFocusListener>()
    private val owners = mutableMapOf<Int, IAppFocusOwnershipCallback>()

    override fun registerFocusListener(callback: IAppFocusListener?, appType: Int) {
        Log.i(TAG, "app_focus.registerFocusListener appType=$appType callback=$callback")
        if (callback != null) listeners.register(callback)
    }

    override fun unregisterFocusListener(callback: IAppFocusListener?, appType: Int) {
        Log.i(TAG, "app_focus.unregisterFocusListener appType=$appType callback=$callback")
        if (callback != null) listeners.unregister(callback)
    }

    override fun getActiveAppTypes(): IntArray {
        val activeTypes = owners.keys.toIntArray()
        Log.i(TAG, "app_focus.getActiveAppTypes count=${activeTypes.size}")
        return activeTypes
    }

    override fun isOwningFocus(callback: IAppFocusOwnershipCallback?, appType: Int): Boolean {
        val owning = callback != null && owners[appType]?.asBinder() == callback.asBinder()
        Log.i(TAG, "app_focus.isOwningFocus appType=$appType owning=$owning callback=$callback")
        return owning
    }

    override fun requestAppFocus(callback: IAppFocusOwnershipCallback?, appType: Int): Int {
        Log.i(TAG, "app_focus.requestAppFocus appType=$appType callback=$callback")
        if (callback == null) return APP_FOCUS_REQUEST_FAILED
        owners[appType] = callback
        notifyFocusChanged(appType, true)
        callback.onAppFocusOwnershipGranted(appType)
        return APP_FOCUS_REQUEST_SUCCEEDED
    }

    override fun abandonAppFocus(callback: IAppFocusOwnershipCallback?, appType: Int) {
        Log.i(TAG, "app_focus.abandonAppFocus appType=$appType callback=$callback")
        if (callback != null && owners[appType]?.asBinder() == callback.asBinder()) {
            owners.remove(appType)
            notifyFocusChanged(appType, false)
            callback.onAppFocusOwnershipLost(appType)
        }
    }

    private fun notifyFocusChanged(appType: Int, active: Boolean) {
        Log.i(TAG, "app_focus.notifyFocusChanged appType=$appType active=$active")
        val count = listeners.beginBroadcast()
        try {
            for (i in 0 until count) {
                listeners.getBroadcastItem(i).onAppFocusChanged(appType, active)
            }
        } finally {
            listeners.finishBroadcast()
        }
    }
}