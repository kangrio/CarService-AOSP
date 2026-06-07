package com.kangrio.carservice.service

import android.content.Context
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.statistic.BYDAutoStatisticDevice
import android.preference.PreferenceManager
import com.kangrio.carservice.provider.EnergyModelFactory
import com.kangrio.carservice.service.listener.StatisticListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class DataCollect(val mContext: CarContext) {
    val TAG = "DataCollect"
    val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)

    private var supportAutoService = false

    private var mBYDAutoStatisticDevice: BYDAutoStatisticDevice? = null
    private var mStatisticListener: StatisticListener? = null

//    private var mBYDAutoChargingDevice: BYDAutoChargingDevice? = null
//    private var mChargingListener: ChargingListener? = null
//
//    private var mBYDAutoAcDevice: BYDAutoAcDevice? = null
//    private var mAcListener: AcListener? = null
//
//    private var mBYDAutoInstrumentDevice: BYDAutoInstrumentDevice? = null
//    private var mInstrumentListener: InstrumentListener? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        supportAutoService = isSupportBydAutoService()
        if (supportAutoService) {
            mBYDAutoStatisticDevice = BYDAutoStatisticDevice.getInstance(mContext)
//            mBYDAutoChargingDevice = BYDAutoChargingDevice.getInstance(mContext)
//            mBYDAutoAcDevice = BYDAutoAcDevice.getInstance(mContext)
//            mBYDAutoInstrumentDevice = BYDAutoInstrumentDevice.getInstance(mContext)
            mStatisticListener = StatisticListener()
//            mChargingListener = ChargingListener()
//            mAcListener = AcListener()
//            mInstrumentListener = InstrumentListener()
        }
        if (!supportAutoService) {
            startSimulate()
        }
    }

    private fun startSimulate() {
        scope.launch {
            var batteryPercent = EnergyModelFactory.getBatteryPercentage()
            while (true) {
                batteryPercent -= 1
                if (batteryPercent < 0) batteryPercent = Random.nextDouble(10.0, 90.0).toInt()
                EnergyModelFactory.setBatteryPercentage(batteryPercent)
                delay(60_000)
            }
        }
    }

    private fun startListener() {
        if (!supportAutoService) return

        mBYDAutoStatisticDevice?.registerListener(
            mStatisticListener,
            intArrayOf(
                BYDAutoFeatureIds.STATISTIC_SOC_BATTERY_PERCENTAGE,
                BYDAutoFeatureIds.STATISTIC_TOTAL_ELEC_CONSUMPTION
            )
        )

//        mBYDAutoChargingDevice?.registerListener(mChargingListener)
//        mBYDAutoAcDevice?.registerListener(mAcListener)
//        mBYDAutoInstrumentDevice?.registerListener(mInstrumentListener)
    }

    private fun stopListener() {
        if (!supportAutoService) return

        mBYDAutoStatisticDevice?.unregisterListener(mStatisticListener)
//        mBYDAutoChargingDevice?.unregisterListener(mChargingListener)
//        mBYDAutoAcDevice?.unregisterListener(mAcListener)
//        mBYDAutoInstrumentDevice?.unregisterListener(mInstrumentListener)
    }

    private fun isSupportBydAutoService(): Boolean {
        return mContext.getSystemService("auto") != null
    }

    companion object {
        private var instance: DataCollect? = null

        fun start(context: Context) {
            synchronized(this) {
                if (instance != null) return

                instance = DataCollect(CarContext(context.applicationContext))
                instance?.startListener()
            }
        }

        fun stop() {
            synchronized(this) {
                instance?.stopListener()
                instance = null
            }
        }
    }
}