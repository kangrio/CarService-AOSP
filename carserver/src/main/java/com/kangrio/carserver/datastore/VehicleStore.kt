package com.kangrio.carserver.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object VehicleStore : DataStoreBase("vehicle") {
    val BATTERY_CAPACITY_WH = intPreferencesKey("vehicle_battery_capacity_wh")
    val BATTERY_LEVEL_WH = intPreferencesKey("vehicle_battery_level_wh")
    val BATTERY_LEVEL_PERCENT = floatPreferencesKey("vehicle_battery_level_percent")
    val BATTERY_VOLTAGE_VOLT = intPreferencesKey("vehicle_battery_voltage_volt")
    val BATTERY_CURRENT_AMP = floatPreferencesKey("vehicle_battery_current_amp")
    val BATTERY_HEALTH_PERCENT = floatPreferencesKey("vehicle_battery_health_percent")
    val BATTERY_MAX_CHARGE_RATE_WH = intPreferencesKey("vehicle_battery_max_charge_rate_wh")
    val BATTERY_TEMP_CELSIUS = floatPreferencesKey("vehicle_battery_temp_celsius")

    val CHARGE_RATE_WH = intPreferencesKey("vehicle_charge_rate_wh")
    val CHARGE_PORT_CONNECTED = booleanPreferencesKey("vehicle_charge_port_connected")

    val SPEED_METER_PER_SEC = floatPreferencesKey("vehicle_speed_meter_per_sec")
    val CONSUMPTION_WH_PER_100KM = intPreferencesKey("vehicle_consumption_wh_per_100km")

    val EXTERNAL_TEMP_CELSIUS = floatPreferencesKey("vehicle_external_temp_celsius")

    val LATITUDE = doublePreferencesKey("vehicle_latitude")
    val LONGITUDE = doublePreferencesKey("vehicle_longitude")
    val ALTITUDE = doublePreferencesKey("vehicle_altitude")

    val CURRENT_GEAR = intPreferencesKey("vehicle_current_gear")
    val IGNITION_STATE = intPreferencesKey("vehicle_ignition_state")
    val PARKING_BRAKE_ON = booleanPreferencesKey("vehicle_parking_brake_on")
    val DRIVER_SEAT = intPreferencesKey("vehicle_driver_seat")
}