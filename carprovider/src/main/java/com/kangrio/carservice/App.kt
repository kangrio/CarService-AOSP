package com.kangrio.carservice

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangrio.carservice.service.CarService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App : Application() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            delay(4000)
            startService()
        }
    }

    fun startService() {
        val serviceIntent = Intent(this, CarService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    companion object {
        private lateinit var instance: App

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}