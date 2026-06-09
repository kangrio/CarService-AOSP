package com.kangrio.carserver.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kangrio.carserver.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration.Companion.milliseconds

open class DataStoreBase(val name: String) {
    private lateinit var dataStore: DataStore<Preferences>

    private val listeners =
        CopyOnWriteArrayList<SharedPreferences.OnSharedPreferenceChangeListener>()

    private val cache = ConcurrentHashMap<Preferences.Key<*>, Any?>()
    private val pending = ConcurrentHashMap<Preferences.Key<*>, Any?>()
    private val pendingRemove = ConcurrentHashMap.newKeySet<Preferences.Key<*>>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var file: File

    fun init(context: Context) {
        dataStore = PreferenceDataStoreFactory.create {
            file = context.preferencesDataStoreFile(name)
            file
        }

        runBlocking(Dispatchers.IO) {
            cache.putAll(dataStore.data.first().asMap())
        }

        scope.launch {
            while (isActive) {
                flush()
                delay(1000.milliseconds)
            }
        }
    }

    fun getAllValue(): Map<Preferences.Key<*>, Any?> {
        return cache
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(
        key: Preferences.Key<T>,
        defaultValue: T
    ): T {
        return cache[key] as? T ?: defaultValue
    }

    fun <T> setValue(
        key: Preferences.Key<T>,
        value: T
    ) {
        val old = cache.put(key, value)

        pending[key] = value
        pendingRemove.remove(key)

        if (old != value) {
            notifyChanged(key.name)
        }
    }

    fun remove(key: Preferences.Key<*>) {
        val existed = cache.remove(key) != null

        pending.remove(key)
        pendingRemove.add(key)

        if (existed) {
            notifyChanged(key.name)
        }
    }

    fun clear() {
        val keys = cache.keys.toList()

        cache.clear()
        pending.clear()
        pendingRemove.addAll(keys)

        keys.forEach {
            notifyChanged(it.name)
        }
    }

    private suspend fun flush() {
        if (pending.isEmpty() && pendingRemove.isEmpty()) return

        val writes = pending.toMap()
        val removes = pendingRemove.toSet()

        pending.clear()
        pendingRemove.clear()

        dataStore.edit { prefs ->
            removes.forEach { key ->
                prefs.remove(key)
            }

            writes.forEach { (key, value) ->
                setPrefValue(prefs, key, value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setPrefValue(
        prefs: MutablePreferences,
        key: Preferences.Key<*>,
        value: Any?
    ) {
        if (value == null) {
            prefs.remove(key)
            return
        }

        when (value) {
            is String -> prefs[key as Preferences.Key<String>] = value
            is Int -> prefs[key as Preferences.Key<Int>] = value
            is Long -> prefs[key as Preferences.Key<Long>] = value
            is Float -> prefs[key as Preferences.Key<Float>] = value
            is Double -> prefs[key as Preferences.Key<Double>] = value
            is Boolean -> prefs[key as Preferences.Key<Boolean>] = value
            is Set<*> -> prefs[key as Preferences.Key<Set<String>>] = value as Set<String>
        }
    }

    fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        listeners += listener
    }

    fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        listeners -= listener
    }

    private fun notifyChanged(key: String) {
        scope.launch(Dispatchers.Main) {
            listeners.forEach {
                LogUtils.i("DataStoreBase", "notifyChanged $key")
                it.onSharedPreferenceChanged(null, key)
            }
        }
    }
}
