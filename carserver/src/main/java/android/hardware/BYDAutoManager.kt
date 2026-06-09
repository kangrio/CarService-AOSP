package android.hardware

import android.content.Context


class BYDAutoManager(context: Context) {
    interface OnBYDAutoListener {
        fun onChanged(device_type: Int, event_type: Int, value: Float, data: Any?)

        fun onChanged(device_type: Int, event_type: Int, value: Int, data: Any?)

        fun onChanged(device_type: Int, event_type: Int, value: ByteArray?, data: Any?)

        fun onError(errCode: Int, errMessage: String?)
    }

    fun updateApiStrategy(name: String, changeItems: List<String>) {
        throw UnsupportedOperationException("Stub!")
    }

    fun enableDevice(deviceID: Int, featureIDs: IntArray): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun enableDevice(deviceID: Int): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun disableDevice(deviceID: Int): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun getInt(deviceType: Int, eventType: Int): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun setInt(deviceType: Int, eventType: Int, value: Int): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun setBuffer(deviceType: Int, eventType: Int, value: ByteArray?): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun getDouble(deviceType: Int, eventType: Int): Float {
        throw UnsupportedOperationException("Stub!")
    }

    fun setDouble(deviceType: Int, eventType: Int, value: Double): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun getIntArray(deviceType: Int, eventType: IntArray?): IntArray {
        throw UnsupportedOperationException("Stub!")
    }

    fun getBuffer(deviceType: Int, eventType: Int): ByteArray {
        throw UnsupportedOperationException("Stub!")
    }

    fun setIntArray(deviceType: Int, eventType: IntArray?, value: IntArray?): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun getDoubleArray(deviceType: Int, eventType: IntArray?): FloatArray {
        throw UnsupportedOperationException("Stub!")
    }

    fun setDoubleArray(deviceType: Int, eventType: IntArray?, value: FloatArray?): Int {
        throw UnsupportedOperationException("Stub!")
    }

    fun registerListener(listener: OnBYDAutoListener) {
        throw UnsupportedOperationException("Stub!")
    }

    fun unregisterListener(listener: OnBYDAutoListener) {
        throw UnsupportedOperationException("Stub!")
    }
}