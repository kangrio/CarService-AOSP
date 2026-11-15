package com.kangrio.carserver.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kangrio.carserver.datastore.VehicleStore

class DebugDialog(context: Context) : AlertDialog(context) {
    private var textView: TextView = TextView(context).apply {
        setTextIsSelectable(true)
        setPadding(40, 24, 40, 24)
        textSize = 13f
        typeface = Typeface.MONOSPACE
        text = "Loading debug information..."
    }
    private var updatedOn = 0L

    private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (System.currentTimeMillis() - updatedOn < 1000) return@OnSharedPreferenceChangeListener
        updatedOn = System.currentTimeMillis()
        showData()
    }

    init {
        setTitle("Debug Information")
        setView(
            ScrollView(context).apply {
                addView(
                    textView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        )

        setButton(BUTTON_POSITIVE, "Close", null as DialogInterface.OnClickListener?)
    }

    override fun onStart() {
        super.onStart()
        VehicleStore.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        showData()
    }

    private fun showData() {
        textView.text = getDebugInfo()
    }

    override fun onDetachedFromWindow() {
        VehicleStore.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        super.onDetachedFromWindow()
    }

    private fun getDebugInfo(): String {
        return buildString {
            val allInfo = VehicleStore.getAllValue()
            appendLine("========== VEHICLE ==========")
            allInfo.toSortedMap { key, key1 -> key.toString().compareTo(key1.toString()) }.forEach { (key, value) ->
                appendLine("$key: $value")
            }

            appendLine()
            appendLine("========== ANDROID ==========")
            appendLine("Android: ${Build.VERSION.RELEASE}")
            appendLine("SDK: ${Build.VERSION.SDK_INT}")
        }
    }
}