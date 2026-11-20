package com.kangrio.carserver.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.Preferences
import com.kangrio.carserver.datastore.DataStoreBase

inline val <reified T> T.TAG: String
    get() {
        val clazz = T::class.java
        return when {
            clazz.isAnonymousClass -> clazz.name
            clazz.simpleName == "Companion" ->
                clazz.enclosingClass?.simpleName ?: clazz.simpleName

            clazz.name.contains("$") ->
                clazz.enclosingClass?.simpleName ?: clazz.simpleName
            else -> clazz.simpleName
        }
    }

fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

fun View.applySystemBarInsets(
    type: Int = WindowInsetsCompat.Type.systemBars()
) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(type)

        view.setPadding(
            initialLeft + bars.left,
            initialTop + bars.top,
            initialRight + bars.right,
            initialBottom + bars.bottom
        )

        insets
    }
}