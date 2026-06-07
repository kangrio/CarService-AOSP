package com.kangrio.carservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AwakeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action ?: return
        if (action !in ACTION_FILTER) return
    }

    companion object {
        val ACTION_FILTER = arrayOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_POWER_CONNECTED
        )
    }
}