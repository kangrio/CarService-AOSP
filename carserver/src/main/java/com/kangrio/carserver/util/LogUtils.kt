package com.kangrio.carserver.util

import android.util.Log
import com.kangrio.carserver.BuildConfig

object LogUtils {
//    private val DEBUG = BuildConfig.DEBUG
    private const val DEBUG = BuildConfig.BUILD_TYPE != "release"

    fun d(tag: String, msg: String) {
        if (!DEBUG) return

        Log.d(tag, msg)
    }

    fun e(tag: String, msg: String) {
        if (!DEBUG) return

        Log.e(tag, msg)
    }

    fun e(tag: String, msg: String, e: Throwable) {
        if (!DEBUG) return

        Log.e(tag, msg, e)
    }

    fun i(tag: String, msg: String) {
        if (!DEBUG) return

        Log.i(tag, msg)
    }

    fun v(tag: String, msg: String) {
        if (!DEBUG) return

        Log.v(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (!DEBUG) return

        Log.w(tag, msg)
    }

    fun w(tag: String, msg: String, e: Throwable) {
        if (!DEBUG) return

        Log.w(tag, msg, e)
    }
}