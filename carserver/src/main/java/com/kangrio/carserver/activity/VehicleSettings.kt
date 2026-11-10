package com.kangrio.carserver.activity

import android.car.VehicleAreaSeat
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess

class VehicleSettings : AppCompatActivity() {
    private val nameLayout: TextInputLayout by lazy { findViewById(R.id.vehicleNameLayout) }
    private val nameInput: TextInputEditText by lazy { findViewById(R.id.vehicleNameInput) }
    private val capacityLayout: TextInputLayout by lazy { findViewById(R.id.batteryCapacityLayout) }
    private val capacityInput: TextInputEditText by lazy { findViewById(R.id.batteryCapacityInput) }
    private val seatToggleGroup: MaterialButtonToggleGroup by lazy { findViewById(R.id.seatToggleGroup) }
    private val saveButton: MaterialButton by lazy { findViewById(R.id.saveButton) }
    private val vinText: TextView by lazy { findViewById(R.id.vinText) }

    private val energyStatus: TextView by lazy { findViewById(R.id.energyModelStatus) }
    private val importEnergyButton: MaterialButton by lazy { findViewById(R.id.importEnergyModelButton) }
    private val clearEnergyButton: MaterialButton by lazy { findViewById(R.id.clearEnergyModelButton) }

    private val pickEnergyModel =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importEnergyModel(uri)
        }

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

        importEnergyButton.setOnClickListener {
            try {
                pickEnergyModel.launch(arrayOf("application/json", "text/plain", "*/*"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No file picker available on this device", Toast.LENGTH_LONG).show()
            }
        }
        clearEnergyButton.setOnClickListener {
            VehicleStore.ENERGY_MODEL_PATH.set(VehicleStore, "")
            updateEnergyStatus()
            Toast.makeText(this, "Energy model cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEnergyStatus() {
        val modelPath = VehicleStore.ENERGY_MODEL_PATH.getOrDefault(VehicleStore, "")
        val hasModel = modelPath.isNotEmpty()
        energyStatus.text = if (hasModel) {
            "Imported: ${VehicleStore.ENERGY_MODEL_PATH.getOrDefault(VehicleStore, "custom model")}"
        } else {
            "Using default model"
        }
        clearEnergyButton.isEnabled = hasModel
    }

    private fun importEnergyModel(uri: Uri) {
        try {
            val text = contentResolver.openInputStream(uri)
                ?.bufferedReader()?.use { it.readText() }
                ?: throw IllegalStateException("Can't open file")

            if (text.length > MAX_MODEL_BYTES) throw IllegalArgumentException("File too large")
            JSONObject(text) // validate; add checks for your required fields here

            val name = displayName(uri).let {
                if (it.endsWith(".json")) it else "$it.json"
            }

            if (name == "atto3_energy_model.json") throw IllegalArgumentException("Can't overwrite default model, please rename")
            validateEnergyModel(text)

            val file = File(getExternalFilesDir(MODEL_DIR), name).apply { parentFile?.mkdirs(); createNewFile() }
            file.writeText(text)

            VehicleStore.ENERGY_MODEL_PATH.set(VehicleStore, file.absolutePath)
            updateEnergyStatus()
            Toast.makeText(this, "Energy model imported", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid energy model: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun validateEnergyModel(text: String) {
        val jsonObject = JSONObject(text)
        if (!jsonObject.has("vehicleInfo")) throw NoSuchFieldException("vehicleInfo not found")
        if (!jsonObject.has("roadLoad")) throw NoSuchFieldException("roadLoad not found")
        if (!jsonObject.has("drivetrainLosses")) throw NoSuchFieldException("drivetrainLosses not found")
        if (!jsonObject.has("maxRegenPower")) throw NoSuchFieldException("maxRegenPower not found")
        if (!jsonObject.has("batteryLosses")) throw NoSuchFieldException("batteryLosses not found")
        if (!jsonObject.has("auxiliaryDrainAtTime")) throw NoSuchFieldException("auxiliaryDrainAtTime not found")
        if (!jsonObject.has("auxiliaryDrainAtTemp")) throw NoSuchFieldException("auxiliaryDrainAtTemp not found")
        if (!jsonObject.has("chargingCurves")) throw NoSuchFieldException("chargingCurves not found")
    }

    private fun displayName(uri: Uri): String =
        contentResolver.query(uri, null, null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && c.moveToFirst()) c.getString(i) else null
        } ?: "custom model"

    private fun loadValues() {
        updateEnergyStatus()
        nameInput.setText(VehicleStore.VEHICLE_NAME.getOrDefault(VehicleStore, ""))

        val capacity = VehicleStore.BATTERY_CAPACITY_WH.getOrDefault(VehicleStore, 0)
        if (capacity > 0) capacityInput.setText(capacity.toString())
        updateCapacityHelper()

        val seat = VehicleStore.DRIVER_SEAT.getOrDefault(VehicleStore, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        seatToggleGroup.check(
            if (seat == VehicleAreaSeat.SEAT_ROW_1_RIGHT) R.id.seatRightButton else R.id.seatLeftButton
        )

        vinText.text = "virtual VIN: ${VehicleStore.VEHICLE_VIN.getOrDefault(VehicleStore, "-")}"
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
        private const val MODEL_DIR = "model"
        const val MIN_CAPACITY_WH = 1_000
        const val MAX_CAPACITY_WH = 300_000
        const val MAX_MODEL_BYTES = 1_000_000
    }
}