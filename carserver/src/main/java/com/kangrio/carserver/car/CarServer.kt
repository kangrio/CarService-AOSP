package com.kangrio.carserver.car

import android.car.Car
import android.car.ICar
import android.os.IBinder
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.byd.PropertyUpdater
import com.kangrio.carserver.car.service.AppFocusService
import com.kangrio.carserver.car.service.EmptyNavigationService
import com.kangrio.carserver.car.service.EmptyPowerService
import com.kangrio.carserver.car.service.EmptySensorService
import com.kangrio.carserver.car.service.EmptyVmsSubscriberService
import com.kangrio.carserver.car.service.UxRestrictionsService
import com.kangrio.carserver.car.service.property.PropertyService

class CarServer : ICar.Stub() {
    private val propertyService = PropertyService()
    private val sensorService = EmptySensorService()
    private val uxRestrictionsService = UxRestrictionsService()
    private val navigationService = EmptyNavigationService()
    private val powerService = EmptyPowerService()
    private val vmsSubscriberService = EmptyVmsSubscriberService()
    private val appFocusService = AppFocusService()

    private val propertyUpdater = PropertyUpdater(propertyService)

    fun start() {
        propertyUpdater.start()
    }

    fun release() {
        propertyUpdater.release()
    }

    override fun setCarServiceHelper(helper: IBinder?) {
        LogUtils.i(TAG, "setCarServiceHelper helper=$helper")
    }

    override fun setUserLockStatus(userHandle: Int, unlocked: Int) {
        LogUtils.i(TAG, "setUserLockStatus userHandle=$userHandle unlocked=$unlocked")
    }

    override fun onSwitchUser(userHandle: Int) {
        LogUtils.i(TAG, "onSwitchUser userHandle=$userHandle")
    }

    override fun getCarService(serviceName: String?): IBinder? {
        val service = when (serviceName) {
            Car.SENSOR_SERVICE -> sensorService
            Car.CAR_UX_RESTRICTION_SERVICE -> uxRestrictionsService
            Car.CAR_NAVIGATION_SERVICE -> navigationService
            Car.PROPERTY_SERVICE -> propertyService
            Car.POWER_SERVICE -> powerService
            Car.INFO_SERVICE -> propertyService
            Car.APP_FOCUS_SERVICE -> appFocusService
            Car.VMS_SUBSCRIBER_SERVICE -> vmsSubscriberService
            else -> null
        }
        LogUtils.i(TAG, "getCarService serviceName=$serviceName found=${service != null}")
        return service
    }

    override fun getCarConnectionType(): Int {
        LogUtils.i(TAG, "getCarConnectionType type=${Car.CONNECTION_TYPE_EMBEDDED}")
        return Car.CONNECTION_TYPE_EMBEDDED
    }
}