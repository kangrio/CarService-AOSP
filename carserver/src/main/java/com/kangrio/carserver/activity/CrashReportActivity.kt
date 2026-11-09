package com.kangrio.carserver.activity

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.os.Process
import android.widget.ScrollView
import android.widget.TextView
import kotlin.system.exitProcess


class CrashReportActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crash = intent.getBundleExtra("crash")
        val crashText = if (crash != null) {
            crash.getString("crash", "No crash information available")
        } else {
            "No crash information available"
        }

        val textView = TextView(this)
        textView.text = crashText
        textView.setTextIsSelectable(true)
        textView.textSize = 12f
        textView.setPadding(16, 16, 16, 16)
        textView.setTypeface(Typeface.MONOSPACE)

        val scrollView = ScrollView(this)
        scrollView.addView(textView)
        setContentView(scrollView)
    }

    fun killApp() {
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    override fun onPause() {
        super.onPause()
        killApp()
    }
    override fun onDestroy() {
        super.onDestroy()
        killApp()
    }
}