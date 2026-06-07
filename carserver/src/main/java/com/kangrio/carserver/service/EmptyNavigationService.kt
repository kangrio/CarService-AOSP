package com.kangrio.carserver.service

import android.os.IBinder
import android.os.Parcel
import android.util.Log
import com.kangrio.carserver.TAG

class EmptyNavigationService : RawCarBinder(NAVIGATION_DESCRIPTOR) {
    companion object {
        private const val NAVIGATION_DESCRIPTOR =
            "android.car.cluster.renderer.IInstrumentClusterNavigation"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(NAVIGATION_DESCRIPTOR)
        when (code - IBinder.FIRST_CALL_TRANSACTION) {
            0 -> {
                val hasBundle = data.readInt()
                Log.i(TAG, "navigation.onNavigationStateChanged hasBundle=${hasBundle != 0}")
                reply?.writeNoException()
            }
            1 -> {
                Log.i(TAG, "navigation.getInstrumentClusterInfo")
                reply?.writeNoException()
                reply?.writeInt(0)
            }
            else -> {
                Log.w(TAG, "navigation.unknownTransaction code=$code")
                return false
            }
        }
        return true
    }
}