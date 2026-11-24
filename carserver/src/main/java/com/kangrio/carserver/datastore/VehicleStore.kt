package com.kangrio.carserver.datastore

import android.car.FuelType
import android.car.hardware.property.EvChargingConnectorType
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kangrio.carserver.byd.BydServices

object VehicleStore : DataStoreBase("vehicle") {
    val VEHICLE_TYPE_EV = byteArrayOf(FuelType.ELECTRIC.toByte())
    val VEHICLE_TYPE_ICE = byteArrayOf(FuelType.LEADED.toByte())
    val VEHICLE_TYPE_PLUG_IN = byteArrayOf(FuelType.ELECTRIC.toByte(), FuelType.LEADED.toByte())

    const val DATA_METHOD_NATIVE = "NATIVE"
    const val DATA_METHOD_BYD_MATE = "BYD_MATE"
    const val DATA_METHOD_SIMULATOR = "SIMULATOR"

    val BATTERY_CAPACITY_WH = floatPreferencesKey("vehicle_battery_capacity_wh")
    val BATTERY_LEVEL_WH = floatPreferencesKey("vehicle_battery_level_wh")
    val BATTERY_LEVEL_PERCENT = floatPreferencesKey("vehicle_battery_level_percent")
    val BATTERY_VOLTAGE_VOLT = intPreferencesKey("vehicle_battery_voltage_volt")
    val BATTERY_CURRENT_MAH = floatPreferencesKey("vehicle_battery_current_mah")
    val BATTERY_HEALTH_PERCENT = floatPreferencesKey("vehicle_battery_health_percent")
    val BATTERY_MAX_CHARGE_RATE_WH = intPreferencesKey("vehicle_battery_max_charge_rate_wh")
    val BATTERY_TEMP_CELSIUS = floatPreferencesKey("vehicle_battery_temp_celsius")

    val CHARGE_RATE_MWH = floatPreferencesKey("vehicle_charge_rate_mwh")
    val CHARGE_PORT_CONNECTED = booleanPreferencesKey("vehicle_charge_port_connected")

    val SPEED_METER_PER_SEC = floatPreferencesKey("vehicle_speed_meter_per_sec")
    val CONSUMPTION_KM_PER_KWH = floatPreferencesKey("vehicle_consumption_km_per_kwh")

    val EXTERNAL_TEMP_CELSIUS = floatPreferencesKey("vehicle_external_temp_celsius")

    val LATITUDE = doublePreferencesKey("vehicle_latitude")
    val LONGITUDE = doublePreferencesKey("vehicle_longitude")
    val ALTITUDE = doublePreferencesKey("vehicle_altitude")

    val CURRENT_GEAR = intPreferencesKey("vehicle_current_gear")
    val IGNITION_STATE = intPreferencesKey("vehicle_ignition_state")
    val PARKING_BRAKE_ON = booleanPreferencesKey("vehicle_parking_brake_on")
    val DRIVER_SEAT = intPreferencesKey("vehicle_driver_seat")
    val VEHICLE_VIN = stringPreferencesKey("vehicle_vin")
    val VEHICLE_NAME = stringPreferencesKey("vehicle_name")
    val ENERGY_MODEL_PATH = stringPreferencesKey("vehicle_energy_model_json")
    val VEHICLE_LOW_BEAM_STATE = intPreferencesKey("vehicle_low_beam_state")
    val MAPS_STYLE = stringPreferencesKey("vehicle_maps_style")
    val VEHICLE_PLUG_TYPES = byteArrayPreferencesKey("vehicle_plug_types")
    val VEHICLE_TYPES = byteArrayPreferencesKey("vehicle_types")
    val VEHICLE_DATA_METHOD = stringPreferencesKey("vehicle_data_method")
    val VEHICLE_WEBHOOK_PORT = intPreferencesKey("vehicle_webhook_port")

    override fun init(context: Context) {
        super.init(context)
        val vin = buildString(17) { repeat(17) { append("ABCDEFGHJKLMNPRSTUVWXYZ0123456789".random()) } }
        setValue(VEHICLE_VIN, getValue(VEHICLE_VIN, vin))
        setValue(VEHICLE_NAME, getValue(VEHICLE_NAME, "BYD ${vin.substring(0, 4)}"))
        setValue(MAPS_STYLE, getValue(MAPS_STYLE, "auto"))
        val plugTypes = byteArrayOf(
            EvChargingConnectorType.IEC_TYPE_2_AC.toByte(),
            EvChargingConnectorType.IEC_TYPE_2_CCS_DC.toByte()
        )
        setValue(VEHICLE_PLUG_TYPES, getValue(VEHICLE_PLUG_TYPES, plugTypes))
        setValue(VEHICLE_TYPES, getValue(VEHICLE_TYPES, VEHICLE_TYPE_EV))
        setValue(VEHICLE_DATA_METHOD, getValue(VEHICLE_DATA_METHOD, if (BydServices.supportAutoService) DATA_METHOD_NATIVE else DATA_METHOD_SIMULATOR))
    }
}