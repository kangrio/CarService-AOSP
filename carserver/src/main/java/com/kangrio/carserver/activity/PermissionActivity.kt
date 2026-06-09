package com.kangrio.carserver.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.kangrio.carserver.MainActivity

data class PermissionItem(
    val name: String,
    val detail: String,
    val permission: String? = null,
    val isSpecial: Boolean = false
)

class PermissionActivity : AppCompatActivity() {

    private val items = listOf(
        PermissionItem("Notification", "Allow app to show foreground service notification", Manifest.permission.POST_NOTIFICATIONS),
        PermissionItem("Location", "Required for GPS speed and location", Manifest.permission.ACCESS_FINE_LOCATION),
        PermissionItem("Background Location", "Allow GPS when app is in background", Manifest.permission.ACCESS_BACKGROUND_LOCATION),
        PermissionItem("Overlay", "Allow start foreground service in background", isSpecial = true)
    )

    private lateinit var container: LinearLayout

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            render()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        setContentView(container)
        render()
    }

    private fun render() {
        container.removeAllViews()

        items.forEach { item ->
            container.addView(createPermissionRow(item))
        }
        if (items.all { item -> isGranted(item) }) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createPermissionRow(item: PermissionItem): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 24, 0, 24)
        }

        val text = TextView(this).apply {
            text = SpannableString("${item.name}\n${item.detail}").apply {
                setSpan(
                    RelativeSizeSpan(0.75f),
                    item.name.length + 1,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(Color.GRAY),
                    item.name.length + 1,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val switch = SwitchCompat(this).apply {
            isChecked = isGranted(item)
            setOnCheckedChangeListener { _, checked ->
                if (checked && !isGranted(item)) {
                    request(item)
                }
            }
        }

        row.addView(text)
        row.addView(switch)
        return row
    }

    private fun isGranted(item: PermissionItem): Boolean {
        return when {
            item.isSpecial -> Settings.canDrawOverlays(this)
            item.permission == null -> true
            else -> ContextCompat.checkSelfPermission(
                this,
                item.permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun request(item: PermissionItem) {
        when {
            item.isSpecial -> {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }

            item.permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }

            item.permission == Manifest.permission.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermission.launch(item.permission)
                }
            }

            item.permission != null -> {
                requestPermission.launch(item.permission)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }
}