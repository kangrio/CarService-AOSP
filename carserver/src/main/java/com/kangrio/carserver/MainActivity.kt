package com.kangrio.carserver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kangrio.carserver.activity.AbrpSettings
import com.kangrio.carserver.activity.PermissionActivity
import com.kangrio.carserver.activity.VehicleSettings
import com.kangrio.carserver.data.ReleaseInfo
import com.kangrio.carserver.dialog.DebugDialog
import com.kangrio.carserver.dialog.UpdateDialog
import com.kangrio.carserver.ota.OtaUpdater
import com.kangrio.carserver.util.applySystemBarInsets
import com.kangrio.carserver.util.hasLocationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val abrpSettingsButton: Button by lazy { findViewById(R.id.abrpSettingsButton) }
    private val vehicleSettingsButton: Button by lazy { findViewById(R.id.vehicleSettingsButton) }
    private val debugButton: Button by lazy { findViewById(R.id.debugButton) }
    private val checkOtaButton: Button by lazy { findViewById(R.id.checkOtaButton) }
    private var availableUpdate: ReleaseInfo? = null
    private var debugDialog: DebugDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main).applySystemBarInsets()

        if (!hasLocationPermission()) {
            startActivity(Intent(this, PermissionActivity::class.java))
            finishAffinity()
            return
        }

        setAbrpSettingsButton()
        setVehicleSettingsButton()
        setupDebugButton()
        setupCheckOtaButton()
        ensureOtaUpdate()
    }

    fun ensureOtaUpdate() {
        val otaVersion = intent.getStringExtra("EXTRA_OTA_VERSION")
        val otaSize = intent.getLongExtra("EXTRA_OTA_SIZE", 0L)
        val otaName = intent.getStringExtra("EXTRA_OTA_NAME")
        val otaBody = intent.getStringExtra("EXTRA_OTA_BODY")
        val otaUrl = intent.getStringExtra("EXTRA_OTA_URL")
        if (!otaVersion.isNullOrEmpty() && !otaUrl.isNullOrEmpty()) {
            availableUpdate = ReleaseInfo(
                tagName = otaVersion,
                versionName = otaVersion.removePrefix("v").removePrefix("V"),
                title = otaVersion,
                body = otaBody ?: "",
                size = otaSize,
                htmlUrl = "",
                downloadUrl = otaUrl,
                apkName = otaName ?: "app.apk"
            )
            showUpdateDialog()
        }
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

    fun setupDebugButton() {
        debugButton.setOnClickListener {
            debugDialog = DebugDialog(this).apply {
                show()
            }
        }
    }

    fun setupCheckOtaButton() {
        checkOtaButton.setOnClickListener {
            scope.launch {
                val release = OtaUpdater.checkForUpdate(this@MainActivity, force = true)
                if (release != null) {
                    availableUpdate = release
                    showUpdateDialog()
                } else {
                    val msg = "${getString(R.string.app_name)} is up-to-date (v${OtaUpdater.getCurrentVersionName(this@MainActivity)})"
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showUpdateDialog() {
        availableUpdate?.let { release ->
            UpdateDialog(this, release).apply {
                show()
            }
        }
    }

    override fun onDestroy() {
        debugDialog?.dismiss()
        super.onDestroy()
    }
}