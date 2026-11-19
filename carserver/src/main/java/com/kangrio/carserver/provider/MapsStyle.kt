package com.kangrio.carserver.provider

import com.google.maps.automotive.proto.Theme
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.getOrDefault

object MapsStyle {
    const val AUTO = "auto"
    const val BEAM = "beam"
    const val DARK = "dark"
    const val LIGHT = "light"
    val AUTO_THEME: Theme = Theme.getDefaultInstance()

    val DARK_THEME: Theme = Theme.newBuilder()
        .setDarkMode(Theme.DarkMode.ALWAYS_DARK)
        .setMapsStyle(Theme.Style.STANDARD)
        .setTurnCardStyle(Theme.Style.AMBIENT)
        .build()

    val LIGHT_THEME: Theme = Theme.newBuilder()
        .setDarkMode(Theme.DarkMode.SYSTEM)
        .setMapsStyle(Theme.Style.AMBIENT)
        .setTurnCardStyle(Theme.Style.STANDARD)
        .build()

    fun getMapsTheme(): Theme {
        val mapStyle = VehicleStore.MAPS_STYLE.getOrDefault(VehicleStore, "auto")
        val lowBeamOn = VehicleStore.VEHICLE_LOW_BEAM_STATE_ON.getOrDefault(VehicleStore, false)
        return when (mapStyle) {
            AUTO -> AUTO_THEME
            BEAM -> if (lowBeamOn) DARK_THEME else LIGHT_THEME
            DARK -> DARK_THEME
            LIGHT -> LIGHT_THEME
            else -> AUTO_THEME
        }
    }
}