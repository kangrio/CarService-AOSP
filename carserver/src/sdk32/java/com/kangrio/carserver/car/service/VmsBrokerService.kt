package com.kangrio.carserver.car.service

import android.car.vms.IVmsBrokerService
import android.car.vms.IVmsClientCallback
import android.car.vms.VmsAssociatedLayer
import android.car.vms.VmsLayer
import android.car.vms.VmsLayerDependency
import android.car.vms.VmsProviderInfo
import android.car.vms.VmsRegistrationInfo
import android.os.IBinder
import android.os.SharedMemory
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG

class VmsBrokerService : IVmsBrokerService.Stub() {
    override fun getProviderInfo(binder: IBinder?, p1: Int): VmsProviderInfo? {
        LogUtils.i(TAG, "getProviderInfo binder=$binder p1=$p1")
        return null
    }

    override fun publishLargePacket(binder: IBinder?, p1: Int, p2: VmsLayer?, p3: SharedMemory?) {
        LogUtils.i(TAG, "publishLargePacket binder=$binder p1=$p1 p2=$p2 p3=$p3")
    }

    override fun publishPacket(binder: IBinder?, p1: Int, p2: VmsLayer?, p3: ByteArray?) {
        LogUtils.i(TAG, "publishPacket binder=$binder p1=$p1 p2=$p2 p3=$p3")
    }

    override fun registerClient(binder: IBinder?, p1: IVmsClientCallback?, p2: Boolean): VmsRegistrationInfo? {
        LogUtils.i(TAG, "registerClient binder=$binder p1=$p1 p2=$p2")
        return null
    }

    override fun registerProvider(binder: IBinder?, p1: VmsProviderInfo?): Int {
        LogUtils.i(TAG, "registerProvider binder=$binder p1=$p1")
        return 0
    }

    override fun setMonitoringEnabled(binder: IBinder?, p1: Boolean) {
        LogUtils.i(TAG, "setMonitoringEnabled binder=$binder p1=$p1")
    }

    override fun setProviderOfferings(binder: IBinder?, p1: Int, p2: List<VmsLayerDependency?>?) {
        LogUtils.i(TAG, "setProviderOfferings binder=$binder p1=$p1 p2=$p2")
    }

    override fun setSubscriptions(binder: IBinder?, subs: List<VmsAssociatedLayer?>?) {
        LogUtils.i(TAG, "setSubscriptions binder=$binder subs=$subs")
    }

    override fun unregisterClient(binder: IBinder?) {
        LogUtils.i(TAG, "unregisterClient binder=$binder")
    }
}