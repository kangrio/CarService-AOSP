package com.kangrio.carserver.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.kangrio.carserver.data.ReleaseInfo
import com.kangrio.carserver.ota.OtaUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale

class UpdateDialog(context: Context, private val release: ReleaseInfo) : AlertDialog(context) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        setTitle("Update Available: ${release.tagName}")
        setMessage(generateMessage())
        setButton(BUTTON_POSITIVE, "Update", null as DialogInterface.OnClickListener?)
        setButton(BUTTON_NEGATIVE, "Cancel", null as DialogInterface.OnClickListener?)
    }

    override fun onStart() {
        super.onStart()

        getButton(BUTTON_POSITIVE).setOnClickListener {
            scope.launch {
                setMessage("Downloading...")
                val apkFile = OtaUpdater.downloadApk(context, release.downloadUrl, release.apkName) { progress ->
                    scope.launch {
                        val msg = "Downloading... ${formatBytes((progress * release.size).toLong())}, ${String.format(Locale.ROOT, "%.2f", progress * 100)}%"
                        setMessage(msg)
                    }
                }
                if (apkFile != null) {
                    OtaUpdater.installApk(context, apkFile)
                }
                dismiss()
            }
        }

        getButton(BUTTON_NEGATIVE).setOnClickListener {
            dismiss()
        }
    }

    private fun generateMessage(): String {
        return buildString {
            appendLine("New version ${release.tagName} is available.")
            appendLine("File Size: ${formatBytes(release.size)}")
            appendLine()
            appendLine(release.body)
        }
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format("%.1f MB", mb)
    }
}