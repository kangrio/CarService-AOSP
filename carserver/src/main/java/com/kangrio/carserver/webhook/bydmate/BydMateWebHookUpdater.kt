package com.kangrio.carserver.webhook.bydmate

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
import com.kangrio.carserver.App
import com.kangrio.carserver.car.service.property.BasePropertyUpdater
import com.kangrio.carserver.car.service.property.PropertyService
import com.kangrio.carserver.data.BydMate
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getDoubleOrNull
import com.kangrio.carserver.util.getIntOrNull
import com.kangrio.carserver.util.hasLocationPermission
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject

class BydMateWebHookUpdater(propertyService: PropertyService, private val port: Int = 58765) : BasePropertyUpdater(propertyService) {
    companion object {
        private const val PATH = "/webhook"
    }

    private var server: WebHookServer? = null
    private var currentSpeedMps = 0f
    private var currentData = BydMate()
    private var previousData = BydMate()

    init {
        val availablePort = findAvailablePort()
        VehicleStore.setValue(VehicleStore.VEHICLE_WEBHOOK_PORT, availablePort)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location.hasSpeed()) {
                currentSpeedMps = if (location.hasSpeed() && location.speed > 1f) location.speed else 0f
                setSpeedMetersPerSec(currentSpeedMps)
            }
        }

        override fun onProviderDisabled(provider: String) = Unit
        override fun onProviderEnabled(provider: String) = Unit
        override fun onFlushComplete(requestCode: Int) = Unit
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
    }

    private fun findAvailablePort(): Int {
        for (port in port until port + 10) {
            try {
                java.net.ServerSocket(port).use {
                    return port
                }
            } catch (_: java.io.IOException) {
                // Try next port
            }
        }

        throw IllegalStateException("No available webhook port")
    }

    override fun start() {
        if (server != null) return

        try {
            server = WebHookServer().also {
                it.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            }
            LogUtils.i(TAG, "Webhook listening on port $port")
        } catch (e: Exception) {
            server = null
            LogUtils.e(TAG, "Failed to start webhook", e)
        }
        startGpsSpeed()
    }

    override fun release() {
        server?.stop()
        stopGpsSpeed()
        server = null
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

        val ignitionState = VehicleStore.getValue(VehicleStore.IGNITION_STATE, VehicleIgnitionState.UNDEFINED)
        setIgnitionState(ignitionState)

        val chargingPortConnected = VehicleStore.getValue(VehicleStore.CHARGE_PORT_CONNECTED, false)
        setChargingPortConnected(chargingPortConnected)
    }

    @SuppressLint("MissingPermission")
    private fun startGpsSpeed() {
        val context = App.getAppContext()
        if (!context.hasLocationPermission()) return

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2_000L,
            2.0f, locationListener
        )
    }

    private fun stopGpsSpeed() {
        val context = App.getAppContext()
        if (!context.hasLocationPermission()) return
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener)
    }

    /**
     * BydMate not expose these data
     * setConsumptionKmPerKwh(km: Float)
     * setCurrentGear(gear: Int)
     * setIgnitionState(state: Int)
     * setDriverSeat(seat: Int)
     * setLowBeamLightState(state: Int)
     */
    private fun updateProperties(data: BydMate) {
        currentData = currentData.mergeNotsNull(data)

        if (currentData.soc != previousData.soc) {
            setBatteryLevelPercent(currentData.soc?.toFloat() ?: DEFAULT_BATTERY_LEVEL_PERCENT)
        }

        if (currentData.soh != previousData.soh) {
            setBatteryHealthPercent(currentData.soh?.toFloat() ?: 94f)
        }

        if (currentData.is_parked != previousData.is_parked) {
            setParkingBrakeOn(currentData.is_parked == 1)
        }

        if (currentData.voltage != previousData.voltage || currentData.current != previousData.current) {
            setChargingRateMilliWatts(currentData.voltage ?: DEFAULT_BATTERY_VOLTAGE, (currentData.current ?: 0f).toFloat() * -1000f)
        }

        if (currentData.isCharging != previousData.isCharging) {
            setChargingPortConnected(currentData.isCharging == 1)
        }

        previousData = currentData.copy()
        LogUtils.d(TAG, "Telemetry updated: $data")
    }

    private inner class WebHookServer : NanoHTTPD(port) {

        override fun serve(session: IHTTPSession): Response {
            if (session.uri != PATH) {
                return response(
                    Response.Status.NOT_FOUND,
                    "Not Found"
                )
            }

            if (session.method != Method.POST) {
                return response(
                    Response.Status.METHOD_NOT_ALLOWED,
                    "POST required"
                )
            }

            return try {
                val files = HashMap<String, String>()
                session.parseBody(files)

                val body = files["postData"]
                    ?: return response(
                        Response.Status.BAD_REQUEST,
                        "Empty body"
                    )

                val json = JSONObject(body)

                val telemetry = BydMate(
                    soc = json.getIntOrNull("soc"),
                    is_parked = json.getIntOrNull("is_parked"),
                    soh = json.getIntOrNull("soh"),
                    speed = json.getIntOrNull("speed"),
                    voltage = json.getIntOrNull("voltage"),
                    capacity = json.getDoubleOrNull("capacity"),
                    isCharging = json.getIntOrNull("is_charging"),
                    current = json.getDoubleOrNull("current"),
                    power = json.getDoubleOrNull("power"),
                )

                updateProperties(telemetry)

                response(
                    Response.Status.OK,
                    """{"success":true}""",
                    "application/json"
                )

            } catch (e: Exception) {
                LogUtils.e(TAG, "Webhook error", e)

                response(
                    Response.Status.BAD_REQUEST,
                    """{"success":false,"error":"Invalid telemetry"}""",
                    "application/json"
                )
            }
        }

        private fun response(
            status: Response.IStatus,
            body: String,
            contentType: String = MIME_PLAINTEXT
        ): Response {
            return newFixedLengthResponse(status, contentType, body)
        }
    }
}