package com.kangrio.carserver.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsStore : DataStoreBase("settings") {
    val ABRP_API_KEY = stringPreferencesKey("settings_abrp_api_key")
    val ABRP_TOKEN = stringPreferencesKey("settings_abrp_token")
    val ABRP_RUNNING = booleanPreferencesKey("settings_abrp_running")
    val ABRP_AUTO_START = booleanPreferencesKey("settings_abrp_auto_start")

    val LAST_OTA_CHECK_TIME = longPreferencesKey("settings_last_ota_check_time")
    val OTA_LATEST_VERSION = stringPreferencesKey("settings_ota_latest_version")

    init {
        getValue(ABRP_AUTO_START, false)
    }
}