package com.kangrio.carserver.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
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

fun <T> Preferences.Key<T>.getOrDefault(
    store: DataStoreBase,
    defaultValue: T
): T = store.getValue(this, defaultValue)


fun <T> Preferences.Key<T>.set(
    store: DataStoreBase,
    value: T
) = store.setValue(this, value)