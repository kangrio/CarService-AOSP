package com.kangrio.carservice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kangrio.carservice.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class CarService : Service() {
    val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var logcatProcess: Process? = null
    override fun onCreate() {
        super.onCreate()
        startForeground()
        startCollectLog()
        DataCollect.start(this)
    }

    private fun startCollectLog() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        ioScope.launch {
            val logFile = File(getExternalFilesDir(null), "log.txt").apply {
                parentFile?.mkdirs()
                if (!exists()) createNewFile()
            }

            try {
                logFile.appendText("\n--- logcat started: ${System.currentTimeMillis()} ---\n")
                logcatProcess =
                    ProcessBuilder("logcat", "-T", "1", "-f", logFile.absolutePath).start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

//            while (isActive) {
//                if (logcatProcess?.isAlive == false) {
//
//                }
//                delay(10_000)
//            }
        }
    }

    override fun onDestroy() {
        DataCollect.stop()
        logcatProcess?.destroy()
        ioScope.cancel()
        super.onDestroy()
    }

    fun startForeground() {
        val channelId = "car_service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Car Service",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Car Service")
            .setContentText("Running")
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(p0: Intent?): IBinder? = null
}