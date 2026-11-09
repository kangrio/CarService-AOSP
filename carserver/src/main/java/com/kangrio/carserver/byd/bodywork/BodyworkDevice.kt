package com.kangrio.carserver.byd.bodywork

import android.car.VehicleAreaSeat
import android.hardware.bydauto.BYDAutoConstants
import android.hardware.bydauto.BYDAutoFeatureIds
import android.widget.CustomVehicleConfig
import com.kangrio.carserver.App
import com.kangrio.carserver.byd.BydServices

object BodyworkDevice {
    fun getPowerLevel(): Int {
//        if (!BydServices.supportAutoService) return Int.MAX_VALUE

        return BydServices.getBydAutoManager().getInt(
            BYDAutoConstants.BYDAUTO_DEVICE_BODYWORK,
            BYDAutoFeatureIds.BODYWORK_POWER_LEVEL
        )
    }

    fun getDriverSeat(): Int = runCatching {
        val isRightDriver = CustomVehicleConfig.getInstance(App.getAppContext()).isRightDriver
        if (isRightDriver) {
            VehicleAreaSeat.SEAT_ROW_1_RIGHT
        } else {
            VehicleAreaSeat.SEAT_ROW_1_LEFT
        }
    }.getOrElse {
        VehicleAreaSeat.SEAT_ROW_1_LEFT
    }
}