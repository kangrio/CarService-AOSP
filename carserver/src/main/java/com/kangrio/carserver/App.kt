package com.kangrio.carserver

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.kangrio.carserver.datastore.SettingsStore
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.service.CarService
import org.lsposed.hiddenapibypass.HiddenApiBypass


class App : Application() {
    companion object {
        lateinit var instance: App
        fun getAppContext(): App {
            return instance
        }

        fun isBydAuto(): Boolean {
            return instance.getSystemService("auto") != null
        }
    }

    val permissions: Array<String> = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) android.Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).mapNotNull { it }.toTypedArray()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.install(this)
        SettingsStore.init(this)
        VehicleStore.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }
        if (isPermissionGranted()) {
            CarService.start(this)
        }
    }

    fun isPermissionGranted(): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun enforceCallingOrSelfPermission(permission: String, message: String?) {
        if (permission.contains("BYDAUTO")) return
        super.enforceCallingOrSelfPermission(permission, message)
    }
}