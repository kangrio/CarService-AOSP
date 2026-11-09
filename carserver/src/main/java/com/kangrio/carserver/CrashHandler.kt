package com.kangrio.carserver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.kangrio.carserver.activity.CrashReportActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.system.exitProcess

object CrashHandler {
    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        val crashFile = File(context.getExternalFilesDir("log"), "crash.txt").apply {
            parentFile?.mkdirs()
            createNewFile()
        }
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrashLog(crashFile, throwable.stackTraceToString())
            } catch (e: Throwable) {
            }
            try {
                startCrashReport(context, throwable.stackTraceToString())
            } catch (e: Throwable) {
            }
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    private fun startCrashReport(context: Context, text: String) {
        val crash = Bundle()
        crash.putString("crash", text)

        val intent = Intent(context, CrashReportActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("crash", crash)
        context.startActivity(intent)
    }

    private fun saveCrashLog(file: File, text: String) {
        file.appendText(buildCrashLog(text))
    }

    private fun buildCrashLog(text: String): String {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        val logText = "$dateTime\n$text\n===================================\n"
        return logText
    }
}