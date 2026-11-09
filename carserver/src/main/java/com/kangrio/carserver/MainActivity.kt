package com.kangrio.carserver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kangrio.carserver.activity.AbrpSettings
import com.kangrio.carserver.activity.PermissionActivity
import com.kangrio.carserver.activity.VehicleSettings
import com.kangrio.carserver.util.hasLocationPermission

class MainActivity :AppCompatActivity() {
    private val abrpSettingsButton: Button by lazy { findViewById(R.id.abrpSettingsButton) }
    private val vehicleSettingsButton: Button by lazy { findViewById(R.id.vehicleSettingsButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (!hasLocationPermission()) {
            startActivity(Intent(this, PermissionActivity::class.java))
            finishAffinity()
            return
        }

        setAbrpSettingsButton()
        setVehicleSettingsButton()
    }

    fun setAbrpSettingsButton() {
        abrpSettingsButton.setOnClickListener {
            startActivity(Intent(this, AbrpSettings::class.java))
        }
    }

    fun setVehicleSettingsButton() {
        vehicleSettingsButton.setOnClickListener {
            startActivity(Intent(this, VehicleSettings::class.java))
        }
    }
}