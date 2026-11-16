package com.kangrio.carserver.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.net.toUri
import com.kangrio.carserver.R
import com.kangrio.carserver.util.applySystemBarInsets

class SelfCheckActivity : AppCompatActivity() {
    private val linearLayoutMicrog: LinearLayout by lazy { findViewById(R.id.linearLayoutMicrog) }
    private val linearLayoutMicrogCompanion: LinearLayout by lazy { findViewById(R.id.linearLayoutMicrogCompanion) }
    private val linearLayoutMaps: LinearLayout by lazy { findViewById(R.id.linearLayoutMaps) }

    private val microgCheckBox: AppCompatCheckBox by lazy { findViewById(R.id.checkBoxMicrog) }
    private val microgCompanionCheckBox: AppCompatCheckBox by lazy { findViewById(R.id.checkBoxMicrogCompanion) }
    private val mapsCheckBox: AppCompatCheckBox by lazy { findViewById(R.id.checkBoxMaps) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_self_check)
        findViewById<View>(R.id.selfCheck).applySystemBarInsets()

        setupLinearLayoutMicrog()
        setupLinearLayoutMicrogCompanion()
        setupLinearLayoutMaps()
    }

    override fun onResume() {
        super.onResume()
        updateSelfCheck()
    }

    fun updateSelfCheck() {
        microgCheckBox.isChecked = checkMicroG()
        microgCompanionCheckBox.isChecked = checkMicroGCompanion()
        mapsCheckBox.isChecked = checkMaps()
    }

    fun openGStore() {
        AlertDialog.Builder(this)
            .setTitle("Open GStore")
            .setMessage("You need to download GStore and install MicroG from the GStore to continue using this app.")
            .setPositiveButton("Open GStore") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, G_STORE_URL.toUri())
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun openTelegram() {
        AlertDialog.Builder(this)
            .setTitle("Open Telegram")
            .setMessage("You need to download Google Maps from Telegram to continue using this app.")
            .setPositiveButton("Open Telegram") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, TELEGRAM_CHANNEL_LINK.toUri())
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun setupLinearLayoutMicrog() {
        linearLayoutMicrog.setOnClickListener {
            updateSelfCheck()
            if (!microgCheckBox.isChecked) openGStore()
        }
    }

    fun setupLinearLayoutMicrogCompanion() {
        linearLayoutMicrogCompanion.setOnClickListener {
            updateSelfCheck()
            if (!microgCompanionCheckBox.isChecked) openGStore()
        }
    }

    fun setupLinearLayoutMaps() {
        linearLayoutMaps.setOnClickListener {
            updateSelfCheck()
            if (!mapsCheckBox.isChecked) openTelegram()
        }
    }

    fun checkMicroG(): Boolean {
        return try {
            val info = packageManager.getPackageInfo("com.google.android.gms", PackageManager.GET_META_DATA) ?: return false
            val metaData = info.applicationInfo?.metaData ?: return false

            return metaData.containsKey(META_DATA_SPOOFED_CERTIFICATES)
        } catch (e: Throwable) {
            false
        }
    }

    fun checkMicroGCompanion(): Boolean {
        return try {
            val info = packageManager.getPackageInfo("com.android.vending", PackageManager.GET_META_DATA) ?: return false
            val metaData = info.applicationInfo?.metaData ?: return false

            return metaData.containsKey(META_DATA_SPOOFED_CERTIFICATES)
        } catch (e: Throwable) {
            false
        }
    }

    fun checkMaps(): Boolean {
        return try {
            val info = packageManager.getPackageInfo("com.google.android.apps.maps", PackageManager.GET_META_DATA) ?: return false
            val metaData = info.applicationInfo?.metaData ?: return false

            return metaData.containsKey(META_DATA_SPOOFED_CERTIFICATES)
        } catch (e: Throwable) {
            false
        }
    }

    companion object {
        private const val TAG = "SelfCheckActivity"
        private const val G_STORE_URL = "https://github.com/GStore0/GStore/releases/latest"
        private const val META_DATA_SPOOFED_CERTIFICATES = "org.microg.gms.spoofed_certificates"
        private const val TELEGRAM_CHANNEL_LINK = "https://t.me/GStore000"
    }
}