package com.kangrio.carserver.car.service

import android.car.cluster.renderer.IInstrumentClusterNavigation
import android.car.navigation.CarNavigationInstrumentCluster
import android.os.Bundle
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class InstrumentClusterNavigationService : IInstrumentClusterNavigation.Stub() {
    override fun getInstrumentClusterInfo(): CarNavigationInstrumentCluster? {
        LogUtils.i(TAG, "getInstrumentClusterInfo")
        return null
    }

    override fun onNavigationStateChanged(bundle: Bundle?) {
        LogUtils.i(TAG, "onNavigationStateChanged bundle=$bundle")
    }
}
