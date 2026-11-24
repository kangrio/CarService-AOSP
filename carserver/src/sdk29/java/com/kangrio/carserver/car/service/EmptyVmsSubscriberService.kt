package com.kangrio.carserver.car.service

import android.os.Parcel
import com.kangrio.carserver.util.LogUtils

class EmptyVmsSubscriberService : RawCarBinder(VMS_SUBSCRIBER_DESCRIPTOR) {
    companion object {
        private val TAG = "EmptyVmsSubscriberService"
        private const val VMS_SUBSCRIBER_DESCRIPTOR = "android.car.vms.IVmsSubscriberService"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(VMS_SUBSCRIBER_DESCRIPTOR)
        val transaction = code.carTransaction()
        when (transaction) {
            in 0..7 -> {
                LogUtils.i(TAG, "vms.${vmsTransactionName(transaction)}")
                reply.writeNoExceptionIfPresent()
            }
            8 -> {
                LogUtils.i(TAG, "vms.getAvailableLayers")
                reply.writeNoExceptionIfPresent()
                reply?.writeInt(0)
            }
            9 -> {
                val publisherId = data.readInt()
                LogUtils.i(TAG, "vms.getPublisherInfo publisherId=$publisherId")
                reply.writeNoExceptionIfPresent()
                reply?.writeByteArray(null)
            }
            else -> {
                LogUtils.w(TAG, "vms.unknownTransaction code=$code")
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
