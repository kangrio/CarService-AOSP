package com.kangrio.carserver.car.service

import android.car.cluster.renderer.IInstrumentClusterNavigation
import android.car.navigation.CarNavigationInstrumentCluster
import android.os.Bundle
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class InstrumentClusterNavigationService : IInstrumentClusterNavigation.Stub() {
    lateinit var carNavigationInstrumentCluster: CarNavigationInstrumentCluster
    override fun getInstrumentClusterInfo(): CarNavigationInstrumentCluster {
        LogUtils.i(TAG, "getInstrumentClusterInfo")

        if (::carNavigationInstrumentCluster.isInitialized) return carNavigationInstrumentCluster
        carNavigationInstrumentCluster = CarNavigationInstrumentCluster.createCluster(1000)
        return carNavigationInstrumentCluster
    }

    override fun onNavigationStateChanged(bundle: Bundle?) {
        LogUtils.i(TAG, "onNavigationStateChanged bundle=$bundle")
    }
}
