package com.kangrio.carserver.simulator

import android.annotation.SuppressLint
import android.car.VehicleAreaSeat
import android.car.VehicleGear
import android.car.VehicleIgnitionState
import android.car.VehicleLightState
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import com.kangrio.carserver.App
import com.kangrio.carserver.car.service.property.BasePropertyUpdater
import com.kangrio.carserver.car.service.property.PropertyService
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.hasLocationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class SimulatorUpdater(propertyService: PropertyService) : BasePropertyUpdater(propertyService) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var simulateJob: Job? = null

    @Volatile
    private var isStarted = false

    private var currentSpeedMps = 0f
    private val updateIntervalMs = 2_000L

    override fun start() {
        if (isStarted) return
        isStarted = true

        setupInitialProperties()
        startSimulate()
    }

    override fun release() {
        isStarted = false

        stopSimulate()
    }

    override fun setupInitialProperties() {
        val lowBeamStateOn = VehicleStore.getValue(VehicleStore.VEHICLE_LOW_BEAM_STATE, VehicleLightState.OFF)
        setLowBeamLightState(lowBeamStateOn)

        val driverSeat = VehicleStore.getValue(VehicleStore.DRIVER_SEAT, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        setDriverSeat(driverSeat)

        val capacityWh = VehicleStore.getValue(VehicleStore.BATTERY_CAPACITY_WH, DEFAULT_BATTERY_CAPACITY_WH)
        setBatteryCapacityWatts(capacityWh)

        val batteryLevelPercent = VehicleStore.getValue(VehicleStore.BATTERY_LEVEL_PERCENT, DEFAULT_BATTERY_LEVEL_PERCENT)
        setBatteryLevelPercent(batteryLevelPercent)

        val batteryHealthPercent = VehicleStore.getValue(VehicleStore.BATTERY_HEALTH_PERCENT, 94f)
        setBatteryHealthPercent(batteryHealthPercent)

        val speedMeters = VehicleStore.getValue(VehicleStore.SPEED_METER_PER_SEC, 0f)
        setSpeedMetersPerSec(speedMeters)

        val consumptionKmPerKwh = VehicleStore.getValue(VehicleStore.CONSUMPTION_KM_PER_KWH, DEFAULT_CONSUMPTION_KM_PER_KWH)
        setConsumptionKmPerKwh(consumptionKmPerKwh)

        val gear = VehicleStore.getValue(VehicleStore.CURRENT_GEAR, VehicleGear.GEAR_UNKNOWN)
        setCurrentGear(gear)

        val ignitionState  = VehicleStore.getValue(VehicleStore.IGNITION_STATE, VehicleIgnitionState.UNDEFINED)
        setIgnitionState(ignitionState)

        val chargingPortConnected = VehicleStore.getValue(VehicleStore.CHARGE_PORT_CONNECTED, false)
        setChargingPortConnected(chargingPortConnected)
    }

    @SuppressLint("MissingPermission")
    private fun startGpsSpeed() {
        val context = App.getAppContext()
        if (!context.hasLocationPermission()) return

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2_000L,
            2.0f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (location.hasSpeed()) {
                        currentSpeedMps =
                            if (location.hasSpeed() && location.speed > 1f) location.speed else 0f
                        setSpeedMetersPerSec(currentSpeedMps)
                    }
                    VehicleStore.setValue(VehicleStore.LATITUDE, location.latitude)
                    VehicleStore.setValue(VehicleStore.LONGITUDE, location.longitude)
                    VehicleStore.setValue(VehicleStore.ALTITUDE, location.altitude)
                }

                override fun onProviderDisabled(provider: String) = Unit
                override fun onProviderEnabled(provider: String) = Unit
                override fun onFlushComplete(requestCode: Int) = Unit
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            }
        )
    }

    fun startSimulate() {
        fun consumptionFor(speed: Double): Double {
            val speedPenalty = ((speed - 72.0).coerceAtLeast(0.0) / 38.0).pow(2.0) * 4.8
            val cityPenalty = if (speed in 1.0..35.0) 1.8 else 0.0
            val acPenalty = 1.2
            return 14.6 + speedPenalty + cityPenalty + acPenalty
        }

        simulateJob?.cancel()
        simulateJob = scope.launch {
            withContext(Dispatchers.Main) {
                startGpsSpeed()
            }

            var lastTime = SystemClock.elapsedRealtime()

            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val deltaHours = (now - lastTime) / 3_600_000.0
                lastTime = now

                var batteryWh = VehicleStore.getValue(VehicleStore.BATTERY_LEVEL_WH, DEFAULT_BATTERY_CAPACITY_WH).toDouble()

                val speedKmh = currentSpeedMps * 3.6
                val consumptionWhPer100Km = consumptionFor(speedKmh) * 1000.0
                val drainWh = consumptionWhPer100Km * speedKmh / 100.0 * deltaHours

                val tractionPowerKw = consumptionWhPer100Km / 1000.0 * speedKmh / 100.0
                val powerKw = when {
                    speedKmh == 0.0 -> 0.9
                    else -> tractionPowerKw + 0.9
                }

                batteryWh -= drainWh

                if (batteryWh < 0) {
                    batteryWh = Random.nextDouble(10_000.0, DEFAULT_BATTERY_CAPACITY_WH.toDouble())
                }
                val batteryPercent = (batteryWh.toFloat() / VehicleStore.getValue(
                    VehicleStore.BATTERY_CAPACITY_WH, DEFAULT_BATTERY_CAPACITY_WH).toFloat() * 100f)

                val volt = DEFAULT_BATTERY_VOLTAGE
                val mAh = powerKw * 1_000_000.0 / volt
                setChargingRateMilliWatts(volt, mAh.toFloat() * -1)

                setBatteryLevelPercent(batteryPercent)
                setConsumptionKmPerKwh(100_000f / consumptionWhPer100Km.toFloat())

                var headLightState = VehicleStore.getValue(VehicleStore.VEHICLE_LOW_BEAM_STATE, VehicleLightState.OFF)
                headLightState = (headLightState + 1) % 2
                setLowBeamLightState(headLightState)
                delay(updateIntervalMs.milliseconds)
            }
        }
    }

    fun stopSimulate() {
        simulateJob?.cancel()
        simulateJob = null
    }
}