package com.kangrio.carserver.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import com.google.maps.automotive.proto.AdditionalFeatureSettings
import com.google.maps.automotive.proto.ApplicationUiSettings
import com.google.maps.automotive.proto.AssistedDrivingProfileOuterClass
import com.google.maps.automotive.proto.AssistedDrivingState
import com.google.maps.automotive.proto.AutoAddChargingStopsSettings
import com.google.maps.automotive.proto.CenterDisplayScreenSettings
import com.google.maps.automotive.proto.CustomStyleSettings
import com.google.maps.automotive.proto.DisplayScaling
import com.google.maps.automotive.proto.EvTripSettings
import com.google.maps.automotive.proto.FeatureSuppression
import com.google.maps.automotive.proto.InstrumentClusterSettings
import com.google.maps.automotive.proto.MapsSettings
import com.google.maps.automotive.proto.PaymentNetworkSettings
import com.google.maps.automotive.proto.StateOfChargeSettings
import com.google.maps.automotive.proto.Theme
import com.google.maps.automotive.proto.TouchScreenInteractability
import com.kangrio.carserver.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MapsProvider : ContentProvider() {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun delete(
        p0: Uri,
        p1: String?,
        p2: Array<out String?>?
    ): Int {
        LogUtils.d("MapsProvider", "delete:calling:${callingPackage}")
        return 0
    }

    override fun getType(p0: Uri): String? {
        LogUtils.d("MapsProvider", "getType:calling:${callingPackage}")
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        LogUtils.d("MapsProvider", "insert:calling:${callingPackage}")
        return null
    }

    override fun onCreate(): Boolean {
        LogUtils.d("MapsProvider", "onCreate:calling:${callingPackage}")
        return true
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        LogUtils.d("MapsProvider", "openFile uri=$uri mode=$mode caller=$callingPackage")
        if (callingPackage != "com.google.android.apps.maps") {
            throw SecurityException("Unauthorized access from: $callingPackage")
        }
        val pipe = ParcelFileDescriptor.createPipe()
        val readSide = pipe[0]
        val writeSide = pipe[1]


        val content = when (uri.authority) {
            "kangrio_google_maps_energy" -> when (uri.path) {
                "/model" -> EnergyModelFactory.getEnergyModel()
                "/forecast" -> {
                    scope.launch {
                        try {
                            val autoCloseInputStream =
                                ParcelFileDescriptor.AutoCloseInputStream(readSide)
                            autoCloseInputStream.close()
                            context?.contentResolver?.notifyChange(
                                "content://kangrio_google_maps_energy/forecast".toUri(),
                                null
                            )
                        } finally {
                        }
                    }
                    return writeSide
                }
                else -> return null
            }

//            "kangrio_google_maps_vehicle_profile" -> when (uri.path) {
//                "/content" -> AssistedDrivingProfileOuterClass
//                    .AssistedDrivingProfile
//                    .newBuilder()
//                    .build()
//                else -> return null
//            }
//
//            "kangrio_google_maps_settings" -> when (uri.path) {
//                "/content" ->
//                    MapsSettings
//                        .newBuilder()
//                        .build()
//
//                "/ev_trip" -> EvTripSettings
//                    .newBuilder()
//                    .build()
//
//                "/ev_payment_network" -> PaymentNetworkSettings
//                    .newBuilder()
//                    .build()
//
//                else -> return null
//            }
//
//            "kangrio_google_maps_assisted_driving" -> when (uri.path) {
//                "/profile" -> AssistedDrivingProfileOuterClass.AssistedDrivingProfile
//                    .newBuilder()
//                    .build()
//
//                "/state" -> AssistedDrivingState
//                    .newBuilder()
//                    .setState(
//                        AssistedDrivingState.State.AVAILABLE
//                    )
//                    .build()
//
//                else -> return null
//            }

            "kangrio_google_maps_vehicle_profile" -> when (uri.path) {
                "/content" -> AssistedDrivingProfileOuterClass
                    .AssistedDrivingProfile
                    .newBuilder()
                    .setDatasetId("your_dataset_id")
                    .build()
                else -> return null
            }

            "kangrio_google_maps_settings" -> when (uri.path) {
                "/content" -> MapsSettings
                    .newBuilder()
                    .setApplicationUiSettings(
                        ApplicationUiSettings.newBuilder()
                            .setExceedingSpeedLimitAudibility(FeatureSuppression.DEFAULT)
                            .setRoadSpeedLimitVisibility(FeatureSuppression.DEFAULT)
                            .setUpcomingSafetyCameraAudibility(FeatureSuppression.DEFAULT)
                            .setUpcomingSafetyCameraVisibility(FeatureSuppression.DEFAULT)
                    )
                    .setCenterDisplayScreenSettings(
                        CenterDisplayScreenSettings.newBuilder()
                            .setTheme(
                                Theme.newBuilder()
                                    .setDarkMode(Theme.DarkMode.SYSTEM)
                                    .setMapsStyle(Theme.Style.STANDARD)
                                    .setTurnCardStyle(Theme.Style.STANDARD)
                            )
                            .setLimitedWidgetSettings(
                                CenterDisplayScreenSettings.LimitedWidgetSettings.newBuilder()
                                    .setTouchScreenInteractability(
                                        TouchScreenInteractability.ALL_INTERACTIONS_AVAILABLE
                                    )
                                    .setDisplayScaling(
                                        DisplayScaling.newBuilder()
                                            .setDisplayMagnification(1.0f)
                                    )
                            )
                            .setStatusBarStyle(CenterDisplayScreenSettings.StatusBarStyle.DEFAULT)
                            .setSafeMapBounds(
                                CenterDisplayScreenSettings.SafeMapBounds.newBuilder()
                                    .setLeft(0)
                                    .setTop(0)
                                    .setRight(0)
                                    .setBottom(0)
                            )
                            .setSatelliteModeAvailability(FeatureSuppression.DEFAULT)
                            .setAllowMapPanWhenZoomedInJp(FeatureSuppression.DEFAULT)
                            .setDisplayScaling(
                                DisplayScaling.newBuilder()
                                    .setDisplayMagnification(1.0f)
                            )
                    )
                    .setInstrumentClusterSettings(
                        InstrumentClusterSettings.newBuilder()
                            .setTheme(
                                Theme.newBuilder()
                                    .setMapsStyle(Theme.Style.STANDARD)
                                    .setTurnCardStyle(Theme.Style.STANDARD)
                                    .setDarkMode(Theme.DarkMode.SYSTEM)
                            )
                            .setCamera(InstrumentClusterSettings.Camera.DEFAULT_CAMERA_TYPE)
                            .setFeatureSet(InstrumentClusterSettings.FeatureSet.DEFAULT_MAP_ONLY)
                            .setDisplayScaling(
                                DisplayScaling.newBuilder()
                                    .setDisplayMagnification(1.0f)
                            )
                            .setTurnCardSettings(
                                InstrumentClusterSettings.TurnCardSettings.newBuilder()
                                    .setDisplayScaling(
                                        DisplayScaling.newBuilder()
                                            .setDisplayMagnification(1.0f)
                                    )
                            )
                    )
                    .setCustomStyleSettings(
                        CustomStyleSettings.newBuilder()
                            .setCustomStyleId("")
                    )
                    .setAdditionalFeatureSettings(
                        AdditionalFeatureSettings.newBuilder()
                        // add AdditionalFeature entries as needed
                    )
                    .build()

                "/ev_trip" -> EvTripSettings
                    .newBuilder()
                    .setStateOfChargeSettings(
                        StateOfChargeSettings.newBuilder()
                            .setMinChargingStopArrivalChargePercentage(20)
                            .setMinDestinationArrivalChargePercentage(10)
                            .setLowBatteryAlertWhenNotNavigatingThresholdPercentage(15) // range: 5–25
                    )
                    .setAutoAddChargingStopsSettings(
                        AutoAddChargingStopsSettings.newBuilder()
                            .setStoppingPenaltyAtChargingStopMinutes(10)
                    )
                    .build()

                "/ev_payment_network" -> PaymentNetworkSettings
                    .newBuilder()
                    .addSubscribedOemPaymentNetwork(
                        PaymentNetworkSettings.PaymentNetwork.newBuilder()
                            .setPaymentNetworkId("oem_id:network_id")
                    )
                    .setPlugAndChargeCapability(
                        PaymentNetworkSettings.PlugAndChargeCapability.ISO_15118_2
                    )
                    .build()

                else -> return null
            }

            "kangrio_google_maps_assisted_driving" -> when (uri.path) {
                "/profile" -> AssistedDrivingProfileOuterClass
                    .AssistedDrivingProfile
                    .newBuilder()
                    .setDatasetId("your_dataset_id")
                    .build()

                "/state" -> AssistedDrivingState
                    .newBuilder()
                    .setState(AssistedDrivingState.State.AVAILABLE)
                    .build()

                else -> return null
            }

            else -> return null
        }

        scope.launch {
            ParcelFileDescriptor.AutoCloseOutputStream(writeSide).use { out ->
                content.writeTo(out)
            }
        }

        return readSide
    }

    override fun openFile(
        uri: Uri,
        mode: String,
        signal: CancellationSignal?
    ): ParcelFileDescriptor? {
        LogUtils.d("MapsProvider", "openFile:calling:${callingPackage}")
        return super.openFile(uri, mode, signal)
    }

    override fun query(
        p0: Uri,
        p1: Array<out String?>?,
        p2: String?,
        p3: Array<out String?>?,
        p4: String?
    ): Cursor? {
        LogUtils.d("MapsProvider", "query:calling:${callingPackage}")
        return null
    }

    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?
    ): Int {
        LogUtils.d("MapsProvider", "update:calling:${callingPackage}")
        return 0
    }
}