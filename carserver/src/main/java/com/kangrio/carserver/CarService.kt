package com.kangrio.carserver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.car.R

class CarService : Service() {
    private val carServer = CarServer()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        Handler(Looper.getMainLooper()).postDelayed({
            startForeground()
        }, 4000)
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

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "onBind action=${intent?.action}")
        return carServer
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind action=${intent?.action}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }
}
