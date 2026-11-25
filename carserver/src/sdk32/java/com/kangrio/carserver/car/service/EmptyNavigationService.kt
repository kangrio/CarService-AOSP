package com.kangrio.carserver.car.service

import android.os.Parcel
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class EmptyNavigationService : RawCarBinder(NAVIGATION_DESCRIPTOR) {
    companion object {
        private const val NAVIGATION_DESCRIPTOR =
            "android.car.cluster.renderer.IInstrumentClusterNavigation"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(NAVIGATION_DESCRIPTOR)
        when (code.carTransaction()) {
            0 -> {
                val hasBundle = data.readInt()
                LogUtils.i(TAG, "navigation.onNavigationStateChanged hasBundle=${hasBundle != 0}")
                reply.writeNoExceptionIfPresent()
            }
            1 -> {
                LogUtils.i(TAG, "navigation.getInstrumentClusterInfo")
                reply.writeNoExceptionIfPresent()
                reply?.writeInt(0)
            }
            else -> {
                LogUtils.w(TAG, "navigation.unknownTransaction code=$code")
                return false
            }
        }
        return true
    }
}
