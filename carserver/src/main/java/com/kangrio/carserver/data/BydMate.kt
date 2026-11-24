package com.kangrio.carserver.data

import org.json.JSONObject

data class BydMate(
    var soc: Int? = null,
    var is_parked: Int? = null,
    var soh: Int? = null,
    var speed: Int? = null,
    var voltage: Int? = null,
    var capacity: Double? = null,
    var isCharging: Int? = null,
    var current: Double? = null,
    var power: Double? = null,
) {
    fun toJson(): String = JSONObject()
        .put("soc", soc)
        .put("is_parked", is_parked)
        .put("soh", soh)
        .put("speed", speed)
        .put("voltage", voltage)
        .put("capacity", capacity)
        .put("is_charging", isCharging)
        .put("current", current)
        .put("power", power)
        .toString()

    fun mergeNotsNull(other: BydMate): BydMate {
        return copy(
            soc = other.soc ?: soc,
            is_parked = other.is_parked ?: is_parked,
            soh = other.soh ?: soh,
            speed = other.speed ?: speed,
            voltage = other.voltage ?: voltage,
            capacity = other.capacity ?: capacity,
            isCharging = other.isCharging ?: isCharging,
            current = other.current ?: current,
            power = other.power ?: power
        )
    }
}