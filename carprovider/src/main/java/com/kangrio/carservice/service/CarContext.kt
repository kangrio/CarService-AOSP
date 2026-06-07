package com.kangrio.carservice.service

import android.content.Context
import android.content.MutableContextWrapper

class CarContext(context: Context) : MutableContextWrapper(context.applicationContext ?: context) {
    override fun enforceCallingOrSelfPermission(permission: String, message: String?) {
        if (permission.contains("BYDAUTO")) return
        super.enforceCallingOrSelfPermission(permission, message)
    }
}