package com.kangrio.carserver.service

import android.os.IBinder
import android.os.Parcel
import android.util.Log
import com.kangrio.carserver.TAG

class EmptyPowerService : RawCarBinder(POWER_DESCRIPTOR) {
    companion object {
        private const val POWER_DESCRIPTOR = "android.car.hardware.power.ICarPower"
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface(POWER_DESCRIPTOR)
        val transaction = code - IBinder.FIRST_CALL_TRANSACTION
        when (transaction) {
            0, 1, 3, 5 -> {
                val listener = data.readStrongBinder()
                Log.i(TAG, "power.${powerTransactionName(transaction)} listener=$listener")
            }
            2 -> Log.i(TAG, "power.requestShutdownOnNextSuspend")
            4 -> {
                val seconds = data.readInt()
                Log.i(TAG, "power.scheduleNextWakeupTime seconds=$seconds")
            }
            else -> {
                Log.w(TAG, "power.unknownTransaction code=$code")
                return false
            }
        }
        reply?.writeNoException()
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