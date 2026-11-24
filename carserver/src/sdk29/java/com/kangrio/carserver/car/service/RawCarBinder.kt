package com.kangrio.carserver.car.service

import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

open class RawCarBinder(private val descriptor: String) : Binder() {

    init {
        attachInterface(null, descriptor)
    }

    @Throws(RemoteException::class)
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (code == INTERFACE_TRANSACTION) {
            LogUtils.i(TAG, "$descriptor.INTERFACE_TRANSACTION")
            reply?.writeString(descriptor)
            return true
        }
        return false
    }
}

internal fun Int.carTransaction(): Int = this - IBinder.FIRST_CALL_TRANSACTION

internal fun Parcel?.writeNoExceptionIfPresent() {
    this?.writeNoException()
}
