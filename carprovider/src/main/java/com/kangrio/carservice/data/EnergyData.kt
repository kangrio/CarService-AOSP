package com.kangrio.carservice.data

data class EnergyData(
    val batteryPercent: Int? = null, //
    val batteryCapacityWh: Int? = null, //
    val maximumChargingRateWatts: Int? = null,  //
    val batteryTempCelsius: Float? = null, //
    val externalTempCelsius: Float? = null, //
    val hvacOn: Boolean? = null, //

    val consumptionWatts: Int? = null, //

    // new real data
    val auxiliaryDrainWatts: Int? = null, //
    val chargingPowerWatts: Int? = null, //
    val chargingPortConnected: Boolean? = null //
)