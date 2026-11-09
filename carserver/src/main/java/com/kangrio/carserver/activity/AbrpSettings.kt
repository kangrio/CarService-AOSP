package com.kangrio.carserver.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kangrio.carserver.R
import com.kangrio.carserver.datastore.SettingsStore
import com.kangrio.carserver.service.AbrpTelemetryService
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.set

class AbrpSettings : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val apiKeyInput: EditText by lazy { findViewById(R.id.apiKeyInput) }
    private val tokenInput: EditText by lazy { findViewById(R.id.tokenInput) }
    private val startStopButton: Button by lazy { findViewById(R.id.startStopButton) }
    private val statusText: TextView by lazy { findViewById(R.id.statusText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abrp_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.abrpSettings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SettingsStore.registerOnSharedPreferenceChangeListener(this)

        apiKeyInput.setText(SettingsStore.ABRP_API_KEY.getOrDefault(SettingsStore, ""))
        tokenInput.setText(SettingsStore.ABRP_TOKEN.getOrDefault(SettingsStore, ""))

        startStopButton.setOnClickListener {
            val running = SettingsStore.ABRP_RUNNING.getOrDefault(SettingsStore, false)
            if (running) {
                AbrpTelemetryService.stop(this)
                SettingsStore.ABRP_AUTO_START.set(SettingsStore, false)
            } else {
                SettingsStore.ABRP_API_KEY.set(SettingsStore, apiKeyInput.text.toString().trim())
                SettingsStore.ABRP_TOKEN.set(SettingsStore, tokenInput.text.toString().trim())
                SettingsStore.ABRP_AUTO_START.set(SettingsStore, true)
                startService()
            }
        }

        refreshStatus()
    }

    fun refreshStatus() {
        val running = SettingsStore.ABRP_RUNNING.getOrDefault(SettingsStore, false)
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