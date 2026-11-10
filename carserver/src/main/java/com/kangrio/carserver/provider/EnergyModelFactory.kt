package com.kangrio.carserver.provider

import android.widget.Toast
import androidx.core.net.toUri
import com.google.maps.automotive.proto.FloatWithUncertainty
import com.google.maps.automotive.proto.IntWithUncertainty
import com.google.maps.automotive.proto.RoadLoadForce
import com.google.maps.automotive.proto.VehicleEnergyModel
import com.google.maps.automotive.proto.VehicleInfo.HvacStatus
import com.google.protobuf.util.JsonFormat
import com.kangrio.carserver.App
import com.kangrio.carserver.byd.PropertyUpdater
import com.kangrio.carserver.data.EnergyData
import com.kangrio.carserver.datastore.VehicleStore
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import com.kangrio.carserver.util.getOrDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class EnergyModelFactory private constructor() {
    private val appContext get() = App.getAppContext()

    private lateinit var modelFile: File
    private lateinit var vehicleEnergyModel: VehicleEnergyModel

    private var prevEnergyData = EnergyData()
    private var energyData = EnergyData()

    private var baseRoadLoadConstant = 280f
    private var baseRoadLoadLinear = 1.6f
    private var baseRoadLoadQuadratic = 0.42f
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun initInternal() {
        val defaultEnergyModel = File(appContext.getExternalFilesDir(MODEL_DIR), MODEL_FILE_NAME)
        val customEnergyModel = VehicleStore.ENERGY_MODEL_PATH.getOrDefault(VehicleStore, "")
        val usedModelFile: File = if (customEnergyModel.isEmpty()) {
            defaultEnergyModel
        } else {
            File(customEnergyModel)
        }

        modelFile = usedModelFile
        ensureModelFile()

        vehicleEnergyModel = loadModel()
        cacheBaseRoadLoad(vehicleEnergyModel)

        val batteryCapacityWh = readBatteryCapacityWh()
        val batteryWh = readBatteryWh()
        val consumptionWhPer100Km = readConsumptionWhPer100Km()

        vehicleEnergyModel = vehicleEnergyModel
            .toBuilder()
            .setVehicleInfo(
                vehicleEnergyModel.vehicleInfo.toBuilder()
                    .setBatteryCapacityWh(intUncertainty(batteryCapacityWh))
                    .setBatteryLevelWh(intUncertainty(batteryWh))
                    .build()
            )
            .setRoadLoad(createRoadLoadFromConsumption(consumptionWhPer100Km))
            .build()

        energyData = createEnergyDataFromModel(
            consumptionWh = consumptionWhPer100Km
        )
        scope.launch {
            while (isActive) {
                if (prevEnergyData != energyData) {
                    prevEnergyData = energyData
                    notifyChange()
                }
                delay(NOTIFY_INTERVAL_MS.milliseconds)
            }
        }
    }

    private fun update(data: EnergyData) {
        val oldModel = vehicleEnergyModel
        val oldInfo = oldModel.vehicleInfo

        val modelBuilder = oldModel.toBuilder()
        val infoBuilder = oldInfo.toBuilder()

        data.batteryCapacityWh?.let {
            infoBuilder.setBatteryCapacityWh(intUncertainty(it))
        }

        data.batteryLevelWh?.let { wh ->
            infoBuilder.setBatteryLevelWh(intUncertainty(wh))

            energyData = energyData.copy(
                batteryLevelWh = wh
            )
        }

        data.consumptionWh?.let {
            modelBuilder.setRoadLoad(createRoadLoadFromConsumption(it))
        }

        data.maximumChargingRateWh?.let {
            infoBuilder.setMaximumChargingRateWatts(it)
        }

        data.batteryTempCelsius?.let {
            infoBuilder.setBatteryTempCelsius(it)
        }

        data.externalTempCelsius?.let {
            infoBuilder.setExternalTempCelsius(it)
        }

        data.hvacOn?.let {
            infoBuilder.setHvacStatus(
                if (it) HvacStatus.ON_HVAC_STATUS else HvacStatus.OFF_HVAC_STATUS
            )
        }

        data.chargingPowerWh?.let {
            infoBuilder.setMaximumChargingRateWatts(it.coerceAtLeast(0))
        }

        vehicleEnergyModel = modelBuilder
            .setVehicleInfo(infoBuilder.build())
            .build()

        energyData = energyData.mergeNotsNull(data)
    }

    private fun ensureModelFile() {
        modelFile.parentFile?.mkdirs()

        if (modelFile.exists() && modelFile.length() > 0L) return

        runCatching {
            appContext.assets.open(MODEL_FILE_NAME).use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }.onFailure {
            LogUtils.e(TAG, "copy default model failed", it)
        }
    }

    private fun loadModel(): VehicleEnergyModel {
        return runCatching {
            VehicleEnergyModel.newBuilder().also { builder ->
                JsonFormat.parser().merge(modelFile.readText(), builder)
            }.build()
        }.getOrElse {
            LogUtils.e(TAG, "load model failed", it)
            Toast.makeText(appContext, "load model failed ${it.message}", Toast.LENGTH_LONG).show()
            VehicleEnergyModel.newBuilder().build()
        }
    }

    private fun cacheBaseRoadLoad(model: VehicleEnergyModel) {
        baseRoadLoadConstant = model.roadLoad.constant.mean
        baseRoadLoadLinear = model.roadLoad.linear.mean
        baseRoadLoadQuadratic = model.roadLoad.quadratic.mean
    }

    private fun createEnergyDataFromModel(
        consumptionWh: Int
    ): EnergyData {
        val info = vehicleEnergyModel.vehicleInfo

        return EnergyData(
            batteryLevelWh = info.batteryLevelWh.mean,
            batteryCapacityWh = info.batteryCapacityWh.mean,
            maximumChargingRateWh = info.maximumChargingRateWatts,
            batteryTempCelsius = info.batteryTempCelsius,
            externalTempCelsius = info.externalTempCelsius,
            hvacOn = info.hvacStatus == HvacStatus.ON_HVAC_STATUS,
            consumptionWh = consumptionWh,
            chargingPowerWh = null,
            chargingPortConnected = null
        )
    }

    private fun readBatteryWh(): Int {
        return VehicleStore.BATTERY_LEVEL_WH.getOrDefault(
                VehicleStore,
            PropertyUpdater.DEFAULT_BATTERY_LEVEL_WH
            )
    }

    private fun readBatteryCapacityWh(): Int {
        return VehicleStore.BATTERY_CAPACITY_WH.getOrDefault(
            VehicleStore,
            PropertyUpdater.DEFAULT_BATTERY_CAPACITY_WH
        )
    }

    private fun readConsumptionWhPer100Km(): Int {
        return VehicleStore.CONSUMPTION_WH_PER_100KM.getOrDefault(
                VehicleStore,
            PropertyUpdater.DEFAULT_CONSUMPTION_WH_PER_100KM
        )
    }

    private fun createRoadLoadFromConsumption(whPer100km: Int): RoadLoadForce {
        val scale = (whPer100km.toFloat() / PropertyUpdater.DEFAULT_CONSUMPTION_WH_PER_100KM)
            .coerceIn(0.6f, 1.8f)

        return RoadLoadForce.newBuilder()
            .setConstant(floatUncertainty(baseRoadLoadConstant * scale))
            .setLinear(floatUncertainty(baseRoadLoadLinear * scale))
            .setQuadratic(floatUncertainty(baseRoadLoadQuadratic * scale))
            .build()
    }

    private fun notifyChange() {
        appContext.contentResolver.notifyChange(MODEL_URI.toUri(), null)
    }

    private fun intUncertainty(mean: Int, sd: Float = 0f): IntWithUncertainty {
        return IntWithUncertainty.newBuilder()
            .setMean(mean)
            .setStandardDeviation(sd)
            .build()
    }

    private fun floatUncertainty(mean: Float, sd: Float = 0f): FloatWithUncertainty {
        return FloatWithUncertainty.newBuilder()
            .setMean(mean)
            .setStandardDeviation(sd)
            .build()
    }

    companion object {
        private const val NOTIFY_INTERVAL_MS = 1_000L

        private const val MODEL_DIR = "model"
        private const val MODEL_FILE_NAME = "atto3_energy_model.json"
        private const val MODEL_URI = "content://kangrio_google_maps_energy/model"

        private val instance: EnergyModelFactory by lazy {
            EnergyModelFactory()
        }

        fun init() {
            instance.initInternal()
        }

        fun getEnergyModel(): VehicleEnergyModel = instance.vehicleEnergyModel

        fun setBatteryLevelWh(value: Int) {
            LogUtils.i(TAG, "setBatteryLevelWh $value")
            instance.update(EnergyData(batteryLevelWh = value))
        }

        fun setConsumptionWh(value: Int) {
            LogUtils.i(TAG, "setConsumptionWh $value")
            instance.update(EnergyData(consumptionWh = value))
        }

        fun setMaximumChargingRateWh(value: Int) {
            LogUtils.i(TAG, "setMaximumChargingRateWh $value")
            instance.update(EnergyData(maximumChargingRateWh = value))
        }

        fun setBatteryTempCelsius(value: Float) {
            LogUtils.i(TAG, "setBatteryTempCelsius $value")
            instance.update(EnergyData(batteryTempCelsius = value))
        }

        fun setExternalTempCelsius(value: Float) {
            LogUtils.i(TAG, "setExternalTempCelsius $value")
            instance.update(EnergyData(externalTempCelsius = value))
        }
    }
}