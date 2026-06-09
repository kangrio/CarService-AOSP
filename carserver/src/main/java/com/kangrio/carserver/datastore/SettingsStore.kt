package com.kangrio.carserver.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kangrio.carserver.util.getOrDefault

object SettingsStore : DataStoreBase("settings") {
    val ABRP_API_KEY = stringPreferencesKey("settings_abrp_api_key")
    val ABRP_TOKEN = stringPreferencesKey("settings_abrp_token")
    val ABRP_RUNNING = booleanPreferencesKey("settings_abrp_running")
    val ABRP_AUTO_START = booleanPreferencesKey("settings_abrp_auto_start")

    init {
        ABRP_AUTO_START.getOrDefault(SettingsStore, false)
    }
}