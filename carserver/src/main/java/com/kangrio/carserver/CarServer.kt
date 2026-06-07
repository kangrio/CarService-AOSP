package com.kangrio.carserver

import android.car.Car
import android.car.ICar
import android.os.IBinder
import android.util.Log
import com.kangrio.carserver.service.AppFocusService
import com.kangrio.carserver.service.EmptyNavigationService
import com.kangrio.carserver.service.EmptyPowerService
import com.kangrio.carserver.service.EmptySensorService
import com.kangrio.carserver.service.EmptyVmsSubscriberService
import com.kangrio.carserver.service.PropertyService
import com.kangrio.carserver.service.UxRestrictionsService

class CarServer : ICar.Stub() {
    private val propertyService = PropertyService()
    private val sensorService = EmptySensorService()
    private val uxRestrictionsService = UxRestrictionsService()
    private val navigationService = EmptyNavigationService()
    private val powerService = EmptyPowerService()
    private val vmsSubscriberService = EmptyVmsSubscriberService()
    private val appFocusService = AppFocusService()

    override fun setCarServiceHelper(helper: IBinder?) {
        Log.i(TAG, "setCarServiceHelper helper=$helper")
    }

    override fun setUserLockStatus(userHandle: Int, unlocked: Int) {
        Log.i(TAG, "setUserLockStatus userHandle=$userHandle unlocked=$unlocked")
    }

    override fun onSwitchUser(userHandle: Int) {
        Log.i(TAG, "onSwitchUser userHandle=$userHandle")
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
        Log.i(TAG, "getCarService serviceName=$serviceName found=${service != null}")
        return service
    }

    override fun getCarConnectionType(): Int {
        Log.i(TAG, "getCarConnectionType type=${Car.CONNECTION_TYPE_EMBEDDED}")
        return Car.CONNECTION_TYPE_EMBEDDED
    }
}
