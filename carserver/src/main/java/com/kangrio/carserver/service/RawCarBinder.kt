package com.kangrio.carserver.service

import android.os.Binder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import com.kangrio.carserver.TAG

open class RawCarBinder(private val descriptor: String) : Binder() {

    init {
        attachInterface(null, descriptor)
    }

    @Throws(RemoteException::class)
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (code == INTERFACE_TRANSACTION) {
            Log.i(TAG, "$descriptor.INTERFACE_TRANSACTION")
            reply?.writeString(descriptor)
            return true
        }
        return false
    }
}