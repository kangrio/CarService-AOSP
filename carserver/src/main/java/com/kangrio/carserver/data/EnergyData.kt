package com.kangrio.carserver.data

data class EnergyData(
    val batteryLevelWh: Int? = null, //
    val batteryCapacityWh: Int? = null, //
    val maximumChargingRateWh: Int? = null,  //
    val batteryTempCelsius: Float? = null, //
    val externalTempCelsius: Float? = null, //
    val hvacOn: Boolean? = null, //

    val consumptionWh: Int? = null, //

    // new real data
    val chargingPowerWh: Int? = null, //
    val chargingPortConnected: Boolean? = null //
) {
    fun mergeNotsNull(other: EnergyData): EnergyData {
        return copy(
            batteryLevelWh = other.batteryLevelWh ?: batteryLevelWh,
            batteryCapacityWh = other.batteryCapacityWh ?: batteryCapacityWh,
            maximumChargingRateWh = other.maximumChargingRateWh ?: maximumChargingRateWh,
            batteryTempCelsius = other.batteryTempCelsius ?: batteryTempCelsius,
            externalTempCelsius = other.externalTempCelsius ?: externalTempCelsius,
            hvacOn = other.hvacOn ?: hvacOn,
            consumptionWh = other.consumptionWh ?: consumptionWh,
            chargingPowerWh = other.chargingPowerWh ?: chargingPowerWh,
            chargingPortConnected = other.chargingPortConnected ?: chargingPortConnected
        )
    }
}