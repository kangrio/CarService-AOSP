package com.kangrio.carserver.car.service

import android.os.Parcel
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class EmptyPowerService : RawCarBinder(POWER_DESCRIPTOR) {
    companion object {
        private const val POWER_DESCRIPTOR = "android.car.hardware.power.ICarPower"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(POWER_DESCRIPTOR)
        val transaction = code.carTransaction()
        when (transaction) {
            0, 1, 3, 5 -> {
                val listener = data.readStrongBinder()
                LogUtils.i(TAG, "power.${powerTransactionName(transaction)} listener=$listener")
            }
            2 -> LogUtils.i(TAG, "power.requestShutdownOnNextSuspend")
            4 -> {
                val seconds = data.readInt()
                LogUtils.i(TAG, "power.scheduleNextWakeupTime seconds=$seconds")
            }
            else -> {
                LogUtils.w(TAG, "power.unknownTransaction code=$code")
                return false
            }
        }
        reply.writeNoExceptionIfPresent()
        return true
    }

    private fun powerTransactionName(transaction: Int): String = when (transaction) {
        0 -> "registerListener"
        1 -> "unregisterListener"
        3 -> "finished"
        5 -> "registerListenerWithCompletion"
        else -> "unknown"
    }
}
