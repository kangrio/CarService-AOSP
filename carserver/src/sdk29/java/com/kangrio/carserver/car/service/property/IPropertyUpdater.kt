package com.kangrio.carserver.car.service.property

import com.kangrio.carserver.byd.BydPropertyUpdater
import com.kangrio.carserver.byd.BydServices
import com.kangrio.carserver.simulator.SimulatorUpdater

interface IPropertyUpdater {
    companion object {
        fun create(propertyService: PropertyService): IPropertyUpdater {
            return if (BydServices.supportAutoService) {
                BydPropertyUpdater(propertyService)
            } else {
                SimulatorUpdater(propertyService)
            }
        }
    }

    fun start()
    fun release()
    fun setupInitialProperties()

    fun setGlobalProperty(propId: Int, value: Any)
    fun setBatteryCapacityWatts(watts: Float)
    fun setBatteryLevelPercent(percent: Float)
    fun setBatteryHealthPercent(percent: Float)
    fun setSpeedMetersPerSec(speed: Float)
    fun setConsumptionKmPerKwh(km: Float)
    fun setCurrentGear(gear: Int)
    fun setParkingBrakeOn(on: Boolean)
    fun setIgnitionState(state: Int)
    fun setChargingPortConnected(connected: Boolean)
    fun setDriverSeat(seat: Int)
    fun setLowBeamLightState(state: Int)
    fun setChargingRateMilliWatts(volt: Int? = null, mAh: Float? = null)
}