package com.kangrio.carserver.service

import android.os.IBinder
import android.os.Parcel
import android.util.Log

class EmptySensorService : RawCarBinder(SENSOR_DESCRIPTOR) {
    companion object {
        private const val SENSOR_DESCRIPTOR = "android.car.hardware.ICarSensor"
    }

    private val TAG = "EmptySensorService"
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface("android.car.hardware.ICarSensor")
        when (code - IBinder.FIRST_CALL_TRANSACTION) {
            0 -> {
                Log.i(TAG, "sensor.getSupportedSensors")
                reply?.writeNoException()
                reply?.writeIntArray(IntArray(0))
            }
            1 -> {
                val sensorType = data.readInt()
                val rate = data.readInt()
                val callback = data.readStrongBinder()
                Log.i(
                    TAG,
                    "sensor.registerOrUpdateSensorListener sensorType=$sensorType rate=$rate callback=$callback"
                )
                reply?.writeNoException()
                reply?.writeInt(0)
            }
            2, 4 -> {
                val sensorType = data.readInt()
                Log.i(
                    TAG,
                    "sensor.${if (code - IBinder.FIRST_CALL_TRANSACTION == 2) "getLatestSensorEvent" else "getSensorConfig"} sensorType=$sensorType"
                )
                reply?.writeNoException()
                reply?.writeInt(0)
            }
            3 -> {
                val sensorType = data.readInt()
                val callback = data.readStrongBinder()
                Log.i(
                    TAG,
                    "sensor.unregisterSensorListener sensorType=$sensorType callback=$callback"
                )
                reply?.writeNoException()
            }
            else -> {
                Log.w(TAG, "sensor.unknownTransaction code=$code")
                return false
            }
        }
        return true
    }
}