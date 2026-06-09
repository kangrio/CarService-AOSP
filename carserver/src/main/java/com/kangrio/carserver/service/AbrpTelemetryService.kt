package com.kangrio.carserver.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kangrio.carserver.abrp.AbrpTelemetryClient
import com.kangrio.carserver.abrp.TelemetrySource
import com.kangrio.carserver.datastore.SettingsStore
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.hasLocationPermission
import com.kangrio.carserver.util.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AbrpTelemetryService : Service() {
    private val client = AbrpTelemetryClient()
    private val telemetrySource = TelemetrySource()

    @Volatile
    private var running = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startSending()
    }

    override fun onDestroy() {
        running = false
        scope.cancel()
        SettingsStore.ABRP_RUNNING.set(SettingsStore, false)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startSending() {
        if (running) return

        val apiKey = SettingsStore.ABRP_API_KEY.getOrDefault(SettingsStore, "")
        val token = SettingsStore.ABRP_TOKEN.getOrDefault(SettingsStore, "")
        if (apiKey.isBlank() || token.isBlank()) {
            Log.w(TAG, "ABRP API key or token is missing; service stopped")
            stopSelf()
            return
        }
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission is missing; service stopped")
            stopSelf()
            return
        }

        running = true
        SettingsStore.ABRP_RUNNING.set(SettingsStore, true)
        scope.launch {
            while (isActive) {
                val telemetry = telemetrySource.next()
                try {
                    Log.i(TAG, "Request:  ${telemetry.toJson()}")
                    val result = client.send(apiKey, token, telemetry)
                    Log.i(TAG, "Response: ${result.statusCode}, ${result.body}")
                } catch (error: Exception) {
                    Log.w(TAG, "Send failed: ${error.message ?: error.javaClass.simpleName}", error)
                }

                delay(TelemetrySource.SEND_INTERVAL_SECONDS.seconds)
            }
        }
    }

    companion object {

        fun start(context: Context) {
            context.startService(actionIntent(context, CarService.ACTION_START_ABRP))
        }

        fun stop(context: Context) {
            context.startService(actionIntent(context, CarService.ACTION_STOP_ABRP))
        }

        private fun actionIntent(context: Context, actionName: String): Intent {
            return Intent(context, CarService::class.java).apply {
                action = actionName
            }
        }
    }
}