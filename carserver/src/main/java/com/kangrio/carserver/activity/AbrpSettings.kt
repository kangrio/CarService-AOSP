package com.kangrio.carserver.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kangrio.carserver.R
import com.kangrio.carserver.datastore.SettingsStore
import com.kangrio.carserver.service.AbrpTelemetryService
import com.kangrio.carserver.util.applySystemBarInsets

class AbrpSettings : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val apiKeyInput: EditText by lazy { findViewById(R.id.apiKeyInput) }
    private val tokenInput: EditText by lazy { findViewById(R.id.tokenInput) }
    private val startStopButton: Button by lazy { findViewById(R.id.startStopButton) }
    private val statusText: TextView by lazy { findViewById(R.id.statusText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abrp_settings)
        findViewById<View>(R.id.abrpSettings).applySystemBarInsets()

        SettingsStore.registerOnSharedPreferenceChangeListener(this)

        apiKeyInput.setText(SettingsStore.getValue(SettingsStore.ABRP_API_KEY, ""))
        tokenInput.setText(SettingsStore.getValue(SettingsStore.ABRP_TOKEN, ""))

        startStopButton.setOnClickListener {
            val running = SettingsStore.getValue(SettingsStore.ABRP_RUNNING, false)
            if (running) {
                AbrpTelemetryService.stop(this)
                SettingsStore.setValue(SettingsStore.ABRP_AUTO_START, false)
            } else {
                SettingsStore.setValue(SettingsStore.ABRP_API_KEY, apiKeyInput.text.toString().trim())
                SettingsStore.setValue(SettingsStore.ABRP_TOKEN, tokenInput.text.toString().trim())
                SettingsStore.setValue(SettingsStore.ABRP_AUTO_START, true)
                startService()
            }
        }

        refreshStatus()
    }

    fun refreshStatus() {
        val running = SettingsStore.getValue(SettingsStore.ABRP_RUNNING, false)
        statusText.text = if (running) {
            getString(R.string.abrp_status_running)
        } else {
            getString(R.string.abrp_status_stopped)
        }
        startStopButton.text = if (running) "Stop" else "Start"
    }

    private fun startService() {
        AbrpTelemetryService.start(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        if (key in arrayOf(SettingsStore.ABRP_RUNNING.name)) {
            refreshStatus()
        }
    }
}