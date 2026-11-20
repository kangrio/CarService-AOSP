package com.kangrio.carserver.provider

import android.car.VehicleLightState
import com.google.maps.automotive.proto.Theme
import com.kangrio.carserver.datastore.VehicleStore

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
        val mapStyle = VehicleStore.getValue(VehicleStore.MAPS_STYLE, "auto")
        val lowBeamState = VehicleStore.getValue(VehicleStore.VEHICLE_LOW_BEAM_STATE, VehicleLightState.OFF)
        val lowBeamOn = lowBeamState == VehicleLightState.ON
        return when (mapStyle) {
            AUTO -> AUTO_THEME
            BEAM -> if (lowBeamOn) DARK_THEME else LIGHT_THEME
            DARK -> DARK_THEME
            LIGHT -> LIGHT_THEME
            else -> AUTO_THEME
        }
    }
}