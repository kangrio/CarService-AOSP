package com.kangrio.carserver.activity

import android.car.VehicleAreaSeat
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kangrio.carserver.MainActivity
import com.kangrio.carserver.R
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.set
import kotlin.system.exitProcess

class VehicleSettings : AppCompatActivity() {
    val seatToggleButton: Button by lazy { findViewById(R.id.seatToggleButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vehicleSettings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupSeatToggleButton()
    }

    fun setupSeatToggleButton() {
        updateSeat(VehicleStore.DRIVER_SEAT.getOrDefault(VehicleStore, VehicleAreaSeat.SEAT_ROW_1_LEFT))
        seatToggleButton.setOnClickListener {
            val seat = VehicleStore.DRIVER_SEAT.getOrDefault(VehicleStore, VehicleAreaSeat.SEAT_ROW_1_LEFT)
            updateSeat(if (seat == VehicleAreaSeat.SEAT_ROW_1_LEFT) VehicleAreaSeat.SEAT_ROW_1_RIGHT else VehicleAreaSeat.SEAT_ROW_1_LEFT, true)
        }
    }

    fun updateSeat(seat: Int, restart: Boolean = false) {
        VehicleStore.DRIVER_SEAT.set(VehicleStore, seat)
        val side: String = if (seat == VehicleAreaSeat.SEAT_ROW_1_LEFT) {
            "Left"
        } else {
            "Right"
        }
        seatToggleButton.text = side

        if (restart) {
            AlertDialog.Builder(this)
                .setTitle("Restart App")
                .setMessage("Restart app to apply changes")
                .setPositiveButton("Restart") { _, _ -> restartApp() }
                .setNegativeButton("Later", null)
                .show()
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)

        finishAffinity()
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}