package com.kangrio.carserver.car.service

import android.os.Parcel
import com.kangrio.carserver.util.LogUtils

class EmptySensorService : RawCarBinder(SENSOR_DESCRIPTOR) {
    companion object {
        private const val SENSOR_DESCRIPTOR = "android.car.hardware.ICarSensor"
    }

    private val TAG = "EmptySensorService"
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        if (super.onTransact(code, data, reply, flags)) return true
        data.enforceInterface("android.car.hardware.ICarSensor")
        val transaction = code.carTransaction()
        when (transaction) {
            0 -> {
                LogUtils.i(TAG, "sensor.getSupportedSensors")
                reply.writeNoExceptionIfPresent()
                reply?.writeIntArray(IntArray(0))
            }
            1 -> {
                val sensorType = data.readInt()
                val rate = data.readInt()
                val callback = data.readStrongBinder()
                LogUtils.i(
                    TAG,
                    "sensor.registerOrUpdateSensorListener sensorType=$sensorType rate=$rate callback=$callback"
                )
                reply.writeNoExceptionIfPresent()
                reply?.writeInt(0)
            }
            2, 4 -> {
                val sensorType = data.readInt()
                LogUtils.i(
                    TAG,
                    "sensor.${if (transaction == 2) "getLatestSensorEvent" else "getSensorConfig"} sensorType=$sensorType"
                )
                reply.writeNoExceptionIfPresent()
                reply?.writeInt(0)
            }
            3 -> {
                val sensorType = data.readInt()
                val callback = data.readStrongBinder()
                LogUtils.i(
                    TAG,
                    "sensor.unregisterSensorListener sensorType=$sensorType callback=$callback"
                )
                reply.writeNoExceptionIfPresent()
            }
            else -> {
                LogUtils.w(TAG, "sensor.unknownTransaction code=$code")
                return false
            }
        }
        return true
    }
}
