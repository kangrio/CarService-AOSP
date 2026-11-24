package com.kangrio.carserver.data

import org.json.JSONObject

data class BydMate(
    val soc: Double,
    val power: Double,
    val isCharging: Boolean,
) {
    fun toJson(): String = JSONObject()
        .put("soc", soc)
        .put("power", power)
        .put("is_charging", isCharging)
        .toString()
}