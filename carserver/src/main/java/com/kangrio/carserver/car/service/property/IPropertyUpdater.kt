package com.kangrio.carserver.car.service.property

interface IPropertyUpdater {
    fun setBatteryLevelPercent(percent: Float)
    fun setSpeedMetersPerSec(speed: Float)
    fun setConsumptionWhPer100km(wh: Int)
    fun setCurrentGear(gear: Int)
    fun setParkingBrakeOn(on: Boolean)
    fun setIgnitionState(state: Int)
    fun setChargingPortConnected(connected: Boolean)
    fun setDriverSeat(seat: Int)
}