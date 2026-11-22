package com.kangrio.carserver.activity

import android.car.VehicleAreaSeat
import android.car.hardware.property.EvChargingConnectorType
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kangrio.carserver.MainActivity
import com.kangrio.carserver.R
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.provider.MapsStyle
import com.kangrio.carserver.util.applySystemBarInsets
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
    private val mapsStyleToggleGroup: MaterialButtonToggleGroup by lazy { findViewById(R.id.mapsStyleToggleGroup) }
    private val plugTypesButton: MaterialButton by lazy { findViewById(R.id.plugTypesButton) }
    private val vehicleTypeToggleGroup: MaterialButtonToggleGroup by lazy { findViewById(R.id.vehicleTypeToggleGroup) }

    private val plugTypes = EvChargingConnectorType::class.java.declaredFields.mapNotNull { field ->
        field.name to field.getInt(null).toByte()
    }.toMap()

    private val pickEnergyModel =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importEnergyModel(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_settings)
        findViewById<View>(R.id.vehicleSettings).applySystemBarInsets()

        loadValues()
        setupCapacityHelper()
        setupSaveButton()
        setupImportEnergyButton()

        setupClearEnergyButton()
        setupPlugTypesButton()
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener { save() }
    }

    private fun setupImportEnergyButton() {
        importEnergyButton.setOnClickListener {
            try {
                pickEnergyModel.launch(arrayOf("application/json", "text/plain", "*/*"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No file picker available on this device", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClearEnergyButton() {
        clearEnergyButton.setOnClickListener {
            VehicleStore.setValue(VehicleStore.ENERGY_MODEL_PATH, "")
            updateEnergyStatus()
            Toast.makeText(this, "Energy model cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPlugTypesButton() {
        plugTypesButton.setOnClickListener {
            val selectedTypes = VehicleStore.getValue(VehicleStore.VEHICLE_PLUG_TYPES, byteArrayOf())
            showPlugTypeDialog(selectedTypes.toSet()) {
                VehicleStore.setValue(VehicleStore.VEHICLE_PLUG_TYPES, it.toByteArray())
            }
        }
    }

    private fun showPlugTypeDialog(
        selectedTypes: Set<Byte>,
        onSelectionChanged: (Set<Byte>) -> Unit
    ) {
        val entries = plugTypes.entries.toList()
        val checkedItems = BooleanArray(entries.size) { index ->
            entries[index].value in selectedTypes
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Select plug types")
            .setMultiChoiceItems(
                entries.map { it.key }.toTypedArray(),
                checkedItems
            ) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                val result = entries.mapIndexedNotNull { index, entry ->
                    if (checkedItems[index]) entry.value else null
                }.toSet()

                onSelectionChanged(result)
            }
            .show()
    }

    private fun updateEnergyStatus() {
        val modelPath = VehicleStore.getValue(VehicleStore.ENERGY_MODEL_PATH, "")
        val hasModel = modelPath.isNotEmpty()
        energyStatus.text = if (hasModel) {
            "Imported: ${VehicleStore.getValue(VehicleStore.ENERGY_MODEL_PATH, "custom model")}"
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

            VehicleStore.setValue(VehicleStore.ENERGY_MODEL_PATH, file.absolutePath)
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
        nameInput.setText(VehicleStore.getValue(VehicleStore.VEHICLE_NAME, ""))

        val capacity = VehicleStore.getValue(VehicleStore.BATTERY_CAPACITY_WH, 0f).toInt()
        if (capacity > 0) capacityInput.setText(capacity.toString())
        updateCapacityHelper()

        val seat = VehicleStore.getValue(VehicleStore.DRIVER_SEAT, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        seatToggleGroup.check(
            if (seat == VehicleAreaSeat.SEAT_ROW_1_RIGHT) R.id.seatRightButton else R.id.seatLeftButton
        )
        val mapsStyle = VehicleStore.getValue(VehicleStore.MAPS_STYLE, MapsStyle.AUTO)
        mapsStyleToggleGroup.check(
            when (mapsStyle) {
                MapsStyle.BEAM -> R.id.mapsStyleBeamButton
                MapsStyle.DARK -> R.id.mapsStyleDarkButton
                MapsStyle.LIGHT -> R.id.mapsStyleLightButton
                else -> R.id.mapsStyleAutoButton
            }
        )
        val vehicleType = VehicleStore.getValue(VehicleStore.VEHICLE_TYPES, VehicleStore.VEHICLE_TYPE_EV)
        vehicleTypeToggleGroup.check(
            if (vehicleType.contentEquals(VehicleStore.VEHICLE_TYPE_EV)) {
                R.id.vehicleTypeElectricButton
            } else if (vehicleType.contentEquals(VehicleStore.VEHICLE_TYPE_ICE)) {
                R.id.vehicleTypeIceButton
            } else if (vehicleType.contentEquals(VehicleStore.VEHICLE_TYPE_PLUG_IN)) {
                R.id.vehicleTypePluginButton
            } else {
                R.id.vehicleTypeElectricButton
            }
        )

        vinText.text = "virtual VIN: ${VehicleStore.getValue(VehicleStore.VEHICLE_VIN, "-")}"
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

    private fun selectedMapsStyle(): String =
        when (mapsStyleToggleGroup.checkedButtonId) {
            R.id.mapsStyleBeamButton -> {
                MapsStyle.BEAM
            }
            R.id.mapsStyleDarkButton -> {
                MapsStyle.DARK
            }
            R.id.mapsStyleLightButton -> {
                MapsStyle.LIGHT
            }
            else -> {
                MapsStyle.AUTO
            }
        }

    private fun selectedVehicleType(): ByteArray =
        when (vehicleTypeToggleGroup.checkedButtonId) {
            R.id.vehicleTypeElectricButton -> {
                VehicleStore.VEHICLE_TYPE_EV
            }
            R.id.vehicleTypeIceButton -> {
                VehicleStore.VEHICLE_TYPE_ICE
            }
            R.id.vehicleTypePluginButton -> {
                VehicleStore.VEHICLE_TYPE_PLUG_IN
            }
            else -> {
                VehicleStore.VEHICLE_TYPE_EV
            }
        }

    private fun save() {
        val name = nameInput.text?.toString()?.trim().orEmpty()
        val capacity = capacityInput.text?.toString()?.toFloatOrNull()

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
        val mapsStyle = selectedMapsStyle()
        val vehicleType = selectedVehicleType()
        VehicleStore.setValue(VehicleStore.VEHICLE_NAME, name)
        VehicleStore.setValue(VehicleStore.BATTERY_CAPACITY_WH, capacity!!)
        VehicleStore.setValue(VehicleStore.DRIVER_SEAT, seat)
        VehicleStore.setValue(VehicleStore.MAPS_STYLE, mapsStyle)
        VehicleStore.setValue(VehicleStore.VEHICLE_TYPES, vehicleType)

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        showRestartDialog()
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
        const val MIN_CAPACITY_WH = 1_000f
        const val MAX_CAPACITY_WH = 300_000f
        const val MAX_MODEL_BYTES = 1_000_000
    }
}