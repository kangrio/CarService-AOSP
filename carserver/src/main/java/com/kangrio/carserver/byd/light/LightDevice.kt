package com.kangrio.carserver.byd.light

import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import android.hardware.bydauto.light.BYDAutoLightDevice
import com.kangrio.carserver.byd.BydServices

object LightDevice {
    fun isLowBeamStateOn(): Boolean = runCatching {
        val lowBeamState = BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_LIGHT,
            BYDAutoFeatureIds.LIGHT_LOW_BEAM_LIGHT
        )
        lowBeamState == BYDAutoLightDevice.LIGHT_ON
    }.getOrDefault(false)
}