package com.kangrio.carserver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.kangrio.carserver.R
import com.kangrio.carserver.car.CarServer
import com.kangrio.carserver.datastore.SettingsStore
import com.kangrio.carserver.provider.EnergyModelFactory
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getOrDefault

class CarService : Service() {
    private val carServer = CarServer()

    override fun onCreate() {
        super.onCreate()
        LogUtils.i(TAG, "onCreate")
        startForeground()
        if (SettingsStore.ABRP_AUTO_START.getOrDefault(SettingsStore, false)) {
            AbrpTelemetryService.start(this)
        }

        carServer.start()
        EnergyModelFactory.init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.i(TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            ACTION_START_ABRP -> {
                startService(Intent(this, AbrpTelemetryService::class.java))
            }
            ACTION_STOP_ABRP -> {
                stopService(Intent(this, AbrpTelemetryService::class.java))
            }
        }
        return START_NOT_STICKY
    }

    fun startForeground() {
        val channelId = "car_server"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Car Server",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Car Server")
            .setContentText("Running")
            .setOngoing(true)
            .build()

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                try {
                    startForeground(1, notification)
                }catch (e: Throwable) {
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder {
        LogUtils.i(TAG, "onBind action=${intent?.action}")
        return carServer
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtils.i(TAG, "onUnbind action=${intent?.action}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        LogUtils.i(TAG, "onDestroy")
        AbrpTelemetryService.start(this)
        carServer.release()
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP_ABRP = "com.kangrio.carserver.action.STOP_ABRP"
        const val ACTION_START_ABRP = "com.kangrio.carserver.action.START_ABRP"

        fun start(context: Context) {
            val request: OneTimeWorkRequest =
                OneTimeWorkRequest.Builder(CarServiceWorker::class.java)
                    .addTag("CAR_SERVICE_WORK_TAG")
                    .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}