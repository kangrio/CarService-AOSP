package com.kangrio.carserver.abrp

import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.getOrDefault
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

data class AbrpTelemetry(
    val utc: Long,
    val soc: Double,
    val soh: Double,
    val power: Double,
    val speed: Double,
    val lat: Double,
    val lon: Double,
    val isCharging: Boolean,
    val isDcfc: Boolean,
    val isParked: Boolean,
    val carModel: String,
) {
    fun toJson(): String = JSONObject()
        .put("utc", utc)
        .put("soc", round(soc))
        .put("soh", round(soh))
        .put("speed", round(speed))
        .put("lat", round(lat, 6))
        .put("lon", round(lon, 6))
        .put("is_charging", isCharging)
        .put("is_dcfc", isDcfc)
        .put("is_parked", isParked)
        .put("power", round(power))
        .put("car_model", carModel)
        .toString()

    private fun round(value: Double, decimals: Int = 1): Double =
        String.format(Locale.US, "%.${decimals}f", value).toDouble()
}

class TelemetrySource {

    fun next(): AbrpTelemetry {
        val soc = VehicleStore.BATTERY_LEVEL_PERCENT.getOrDefault(
            VehicleStore,
            100f
        ).toDouble()
        val soh = VehicleStore.BATTERY_HEALTH_PERCENT.getOrDefault(
            VehicleStore,
            92f
        ).toDouble()

        val speed = VehicleStore.SPEED_METER_PER_SEC.getOrDefault(VehicleStore, 0f).toDouble() * 3.6
        val parked = VehicleStore.PARKING_BRAKE_ON.getOrDefault(VehicleStore, true)
        val isCharging = VehicleStore.CHARGE_PORT_CONNECTED.getOrDefault(VehicleStore, false)
        val power = VehicleStore.CHARGE_RATE_WH.getOrDefault(VehicleStore, 0).toDouble() / 1000.0
        val lat = 0.0 // VehicleStore.LATITUDE.getOrDefault(VehicleStore, 0.0)
        val lon = 0.0 // VehicleStore.LONGITUDE.getOrDefault(VehicleStore, 0.0)

        return AbrpTelemetry(
            utc = System.currentTimeMillis() / 1000L,
            soc = soc,
            soh = soh,
            speed = speed,
            lat = lat,
            lon = lon,
            isCharging = isCharging,
            isDcfc = false,
            isParked = parked,
            power = power,
            carModel = CAR_MODEL,
        )
    }

    companion object {
        const val SEND_INTERVAL_SECONDS = 10L
        private const val CAR_MODEL = "BYD Atto 3 Extended Range 2023"
    }
}

data class AbrpSendResult(
    val success: Boolean,
    val statusCode: Int,
    val body: String,
)

class AbrpTelemetryClient {
    fun send(apiKey: String, token: String, telemetry: AbrpTelemetry): AbrpSendResult {
        val form = listOf(
            "api_key" to apiKey,
            "token" to token,
            "tlm" to telemetry.toJson(),
        ).joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }

        val connection =
            (URL("https://api.iternio.com/1/tlm/send").openConnection() as HttpURLConnection)
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.doOutput = true
            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8"
            )
            connection.outputStream.use { output ->
                output.write(form.toByteArray(Charsets.UTF_8))
            }

            val status = connection.responseCode
            val body = if (status in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }
            AbrpSendResult(status in 200..299, status, body)
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, Charsets.UTF_8.name())
}
