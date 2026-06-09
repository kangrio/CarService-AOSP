package com.kangrio.carserver.byd

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import com.kangrio.carserver.App
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.hasLocationPermission
import com.kangrio.carserver.util.set
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

class SimulatorUpdater(val propertyUpdater: PropertyUpdater) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var simulateJob: Job? = null

    private var currentSpeedMps = 0f
    private val updateIntervalMs = 2_000L

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
                        propertyUpdater.setSpeedMetersPerSec(currentSpeedMps)
                    }
                    VehicleStore.LATITUDE.set(VehicleStore, location.latitude)
                    VehicleStore.LONGITUDE.set(VehicleStore, location.longitude)
                    VehicleStore.ALTITUDE.set(VehicleStore, location.altitude)
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

                var batteryWh = VehicleStore.BATTERY_LEVEL_WH.getOrDefault(VehicleStore, PropertyUpdater.DEFAULT_BATTERY_CAPACITY_WH).toDouble()

                val speedKmh = currentSpeedMps * 3.6
                val consumptionWhPer100Km = consumptionFor(speedKmh) * 1000.0
                val drainWh = consumptionWhPer100Km * speedKmh / 100.0 * deltaHours

                val tractionPowerKw = consumptionWhPer100Km / 1000.0 * speedKmh / 100.0
                val power = when {
                    speedKmh == 0.0 -> 0.9
                    else -> tractionPowerKw + 0.9
                }

                batteryWh -= drainWh

                if (batteryWh < 0) {
                    batteryWh = Random.nextDouble(10_000.0, PropertyUpdater.DEFAULT_BATTERY_CAPACITY_WH.toDouble())
                }
                val batteryPercent = (batteryWh.toFloat() / VehicleStore.BATTERY_CAPACITY_WH.getOrDefault(
                    VehicleStore, PropertyUpdater.DEFAULT_BATTERY_CAPACITY_WH).toFloat() * 100f)

                VehicleStore.CHARGE_RATE_WH.set(VehicleStore, (power * 1000).toInt())
                propertyUpdater.setBatteryLevelPercent(batteryPercent)
                propertyUpdater.setConsumptionWhPer100km(consumptionWhPer100Km.toInt())
                delay(updateIntervalMs.milliseconds)
            }
        }
    }

    fun stopSimulate() {
        simulateJob?.cancel()
        simulateJob = null
    }
}