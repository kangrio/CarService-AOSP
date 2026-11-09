package com.kangrio.carserver.activity

import android.car.VehicleAreaSeat
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kangrio.carserver.MainActivity
import com.kangrio.carserver.R
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.getOrDefault
import com.kangrio.carserver.util.set
import kotlin.system.exitProcess

class VehicleSettings : AppCompatActivity() {
    private val nameLayout: TextInputLayout by lazy { findViewById(R.id.vehicleNameLayout) }
    private val nameInput: TextInputEditText by lazy { findViewById(R.id.vehicleNameInput) }
    private val capacityLayout: TextInputLayout by lazy { findViewById(R.id.batteryCapacityLayout) }
    private val capacityInput: TextInputEditText by lazy { findViewById(R.id.batteryCapacityInput) }
    private val seatToggleGroup: MaterialButtonToggleGroup by lazy { findViewById(R.id.seatToggleGroup) }
    private val saveButton: MaterialButton by lazy { findViewById(R.id.saveButton) }
    private val vinText: TextView by lazy { findViewById(R.id.vinText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vehicleSettings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadValues()
        setupCapacityHelper()
        saveButton.setOnClickListener { save() }
    }

    private fun loadValues() {
        nameInput.setText(VehicleStore.VEHICLE_NAME.getOrDefault(VehicleStore, ""))

        val capacity = VehicleStore.BATTERY_CAPACITY_WH.getOrDefault(VehicleStore, 0)
        if (capacity > 0) capacityInput.setText(capacity.toString())
        updateCapacityHelper()

        val seat = VehicleStore.DRIVER_SEAT.getOrDefault(VehicleStore, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        seatToggleGroup.check(
            if (seat == VehicleAreaSeat.SEAT_ROW_1_RIGHT) R.id.seatRightButton else R.id.seatLeftButton
        )

        vinText.text = "VIN: ${VehicleStore.VEHICLE_VIN.getOrDefault(VehicleStore, "-")}"
    }

    private fun setupCapacityHelper() {
        capacityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                capacityLayout.error = null
                updateCapacityHelper()
            }
        })
        nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                nameLayout.error = null
            }
        })
    }

    /** Shows the entered Wh value as kWh underneath the field. */
    private fun updateCapacityHelper() {
        val wh = capacityInput.text?.toString()?.toIntOrNull()
        capacityLayout.helperText = if (wh != null && wh > 0) {
            String.format("≈ %.1f kWh", wh / 1000f)
        } else {
            " "
        }
    }

    private fun selectedSeat(): Int =
        if (seatToggleGroup.checkedButtonId == R.id.seatRightButton) {
            VehicleAreaSeat.SEAT_ROW_1_RIGHT
        } else {
            VehicleAreaSeat.SEAT_ROW_1_LEFT
        }

    private fun save() {
        val name = nameInput.text?.toString()?.trim().orEmpty()
        val capacity = capacityInput.text?.toString()?.toIntOrNull()

        var valid = true
        if (name.isEmpty()) {
            nameLayout.error = "Name can't be empty"
            valid = false
        }
        if (capacity == null || capacity !in MIN_CAPACITY_WH..MAX_CAPACITY_WH) {
            capacityLayout.error = "Enter $MIN_CAPACITY_WH–$MAX_CAPACITY_WH Wh"
            valid = false
        }
        if (!valid) return

        val seat = selectedSeat()
        VehicleStore.VEHICLE_NAME.set(VehicleStore, name)
        VehicleStore.BATTERY_CAPACITY_WH.set(VehicleStore, capacity!!)
        VehicleStore.DRIVER_SEAT.set(VehicleStore, seat)

        showRestartDialog()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Restart App")
            .setMessage("Restart the app to apply it.")
            .setPositiveButton("Restart") { _, _ -> restartApp() }
            .setNegativeButton("Later", null)
            .show()
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

    private companion object {
        const val MIN_CAPACITY_WH = 1_000
        const val MAX_CAPACITY_WH = 300_000
    }
}