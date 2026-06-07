package com.kangrio.carserver.service

import android.os.IBinder
import android.os.Parcel
import android.util.Log

class EmptyVmsSubscriberService : RawCarBinder(VMS_SUBSCRIBER_DESCRIPTOR) {
    companion object {
        private val TAG = "EmptyVmsSubscriberService"
        private const val VMS_SUBSCRIBER_DESCRIPTOR = "android.car.vms.IVmsSubscriberService"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(VMS_SUBSCRIBER_DESCRIPTOR)
        val transaction = code - IBinder.FIRST_CALL_TRANSACTION
        when (transaction) {
            in 0..7 -> {
                Log.i(TAG, "vms.${vmsTransactionName(transaction)}")
                reply?.writeNoException()
            }
            8 -> {
                Log.i(TAG, "vms.getAvailableLayers")
                reply?.writeNoException()
                reply?.writeInt(0)
            }
            9 -> {
                val publisherId = data.readInt()
                Log.i(TAG, "vms.getPublisherInfo publisherId=$publisherId")
                reply?.writeNoException()
                reply?.writeByteArray(null)
            }
            else -> {
                Log.w(TAG, "vms.unknownTransaction code=$code")
                return false
            }
        }
        return true
    }

    private fun vmsTransactionName(transaction: Int): String = when (transaction) {
        0 -> "addVmsSubscriberToNotifications"
        1 -> "addVmsSubscriber"
        2 -> "addVmsSubscriberPassive"
        3 -> "addVmsSubscriberToPublisher"
        4 -> "removeVmsSubscriberToNotifications"
        5 -> "removeVmsSubscriber"
        6 -> "removeVmsSubscriberPassive"
        7 -> "removeVmsSubscriberToPublisher"
        else -> "unknown"
    }
}