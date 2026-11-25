package com.kangrio.carserver.car

import android.car.Car
import android.car.ICar
import android.os.IBinder
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.car.service.AppFocusService
import com.kangrio.carserver.car.service.CarMediaService
import com.kangrio.carserver.car.service.EmptyNavigationService
import com.kangrio.carserver.car.service.EmptyPowerService
import com.kangrio.carserver.car.service.EmptySensorService
import com.kangrio.carserver.car.service.EmptyVmsSubscriberService
import com.kangrio.carserver.car.service.UxRestrictionsService
import com.kangrio.carserver.car.service.property.IPropertyUpdater
import com.kangrio.carserver.car.service.property.PropertyService

class CarServer : ICar.Stub() {
    private val propertyService = PropertyService()
    private val sensorService = EmptySensorService()
    private val uxRestrictionsService = UxRestrictionsService()
    private val navigationService = EmptyNavigationService()
    private val powerService = EmptyPowerService()
    private val vmsSubscriberService = EmptyVmsSubscriberService()
    private val appFocusService = AppFocusService()
    private val carMediaService = CarMediaService()

    private val propertyUpdater = IPropertyUpdater.create(propertyService)

    fun start() {
        propertyUpdater.start()
    }

    fun release() {
        propertyUpdater.release()
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
            Car.CAR_MEDIA_SERVICE -> carMediaService
            else -> null
        }
        LogUtils.i(TAG, "getCarService serviceName=$serviceName found=${service != null}")
        return service
    }

    override fun isFeatureEnabled(feature: String?): Boolean {
        LogUtils.i(TAG, "isFeatureEnabled feature=$feature")
        return false
    }

    override fun setSystemServerConnections(p0: IBinder?, p1: IBinder?) {
        LogUtils.i(TAG, "setSystemServerConnections")
    }

    override fun disableFeature(features: String?): Int {
        LogUtils.i(TAG, "disableFeature features=$features")
        return Car.FEATURE_REQUEST_SUCCESS
    }

    override fun enableFeature(features: String?): Int {
        LogUtils.i(TAG, "enableFeature features=$features")
        return Car.FEATURE_REQUEST_SUCCESS
    }

    override fun getAllEnabledFeatures(): List<String?> {
        LogUtils.i(TAG, "getAllEnabledFeatures")
        return emptyList()
    }

    override fun getAllPendingDisabledFeatures(): List<String?> {
        LogUtils.i(TAG, "getAllPendingDisabledFeatures")
        return emptyList()
    }

    override fun getAllPendingEnabledFeatures(): List<String?> {
        LogUtils.i(TAG, "getAllPendingEnabledFeatures")
        return emptyList()
    }

    override fun getCarConnectionType(): Int {
        LogUtils.i(TAG, "getCarConnectionType type=${Car.CONNECTION_TYPE_EMBEDDED}")
        return Car.CONNECTION_TYPE_EMBEDDED
    }

    override fun getCarManagerClassForFeature(feature: String?): String? {
        LogUtils.i(TAG, "getCarManagerClassForFeature features=$feature")
        return null
    }
}