package com.kangrio.carserver.byd.bodywork

import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import com.kangrio.carserver.byd.BydServices

object BodyworkDevice {
    fun getPowerLevel(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        return BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_BODYWORK,
            BYDAutoFeatureIds.BODYWORK_POWER_LEVEL
        )
    }
}