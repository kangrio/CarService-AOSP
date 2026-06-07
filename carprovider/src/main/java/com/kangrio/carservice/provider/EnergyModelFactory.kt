package com.kangrio.carservice.provider

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.maps.automotive.proto.BatteryLosses
import com.google.maps.automotive.proto.ChargingCurves
import com.google.maps.automotive.proto.DrainAtTime
import com.google.maps.automotive.proto.FloatWithUncertainty
import com.google.maps.automotive.proto.IntWithUncertainty
import com.google.maps.automotive.proto.LossAtPower
import com.google.maps.automotive.proto.MaxRegenPower
import com.google.maps.automotive.proto.RegenerationRateAtEnergyLevel
import com.google.maps.automotive.proto.RoadLoadForce
import com.google.maps.automotive.proto.VehicleEnergyModel
import com.google.maps.automotive.proto.VehicleInfo
import com.google.protobuf.util.JsonFormat
import com.kangrio.carservice.data.EnergyData
import com.kangrio.carservice.service.BydServices
import java.io.File

@SuppressLint("StaticFieldLeak")
class EnergyModelFactory(val appContext: Context) {
    companion object {
        private const val TAG = "EnergyModelFactory"
        private lateinit var instance: EnergyModelFactory

        private const val BATTERY_CAPACITY_WH = 60_480
        private const val DEFAULT_CONSUMPTION_WH_PER_100KM = 14_400
        private const val MASS_KG = 2_160
        private const val PEAK_MOTOR_POWER_WATTS = 150_000
        private const val MAX_DC_CHARGE_WATTS = 88_000

        private const val MODEL_DIR = "model"
        private const val MODEL_FILE_NAME = "atto3_energy_model.json"
        private const val MODEL_URI = "content://kangrio_google_maps_energy/model"

        fun init(context: Context) {
            instance = EnergyModelFactory(context)
            instance.init()
        }

        fun getEnergyModel(): VehicleEnergyModel {
            return instance.mVehicleEnergyModel
        }

        fun setBatteryPercentage(batteryPercent: Int) {
            Log.d("EnergyModelFactory", "setBatteryPercentage: $batteryPercent")
            instance.updateEnergyModel(
                EnergyData(
                    batteryPercent = batteryPercent
                )
            )
        }

        fun getBatteryPercentage(): Int {
            return instance.lastBatteryPercent
        }

        fun setConsumption(consumptionWatts: Int) {
            Log.d("EnergyModelFactory", "setConsumption: $consumptionWatts")
            instance.updateEnergyModel(
                EnergyData(
                    auxiliaryDrainWatts = consumptionWatts
                )
            )
        }

        fun setMaximumChargingRateWatts(maximumChargingRateWatts: Int) {
            instance.updateEnergyModel(
                EnergyData(
                    maximumChargingRateWatts = maximumChargingRateWatts
                )
            )
        }

        fun setBatteryTempCelsius(batteryTempCelsius: Float) {
            instance.updateEnergyModel(
                EnergyData(
                    batteryTempCelsius = batteryTempCelsius
                )
            )
        }

        fun setExternalTempCelsius(externalTempCelsius: Float) {
            instance.updateEnergyModel(
                EnergyData(
                    externalTempCelsius = externalTempCelsius
                )
            )
        }
    }

    private lateinit var defaultModelFile: File

    private lateinit var mVehicleEnergyModel: VehicleEnergyModel

    private var roadLoadConstant = 280f
    private var roadLoadLinear = 1.6f
    private var roadLoadQuadratic = 0.42f

    private var lastBatteryPercent: Int =
        BydServices.getStatisticDevice()?.socBatteryPercentage ?: 100

    private var lastConsumptionWatts: Int =
        ((BydServices.getStatisticDevice()?.lastElecConPHMValue ?: 14.4) * 1_000).toInt()

    private fun init() {
        defaultModelFile = File(
            appContext.getExternalFilesDir(MODEL_DIR),
            MODEL_FILE_NAME
        )

        ensureDefaultModelFile()

        mVehicleEnergyModel = loadModelOrFallback()
        updateRoadLoadCache(mVehicleEnergyModel)
        mVehicleEnergyModel = applyCurrentData(lastBatteryPercent, lastConsumptionWatts)
    }

    private fun updateRoadLoadCache(model: VehicleEnergyModel) {
        roadLoadConstant = model.roadLoad.constant.mean
        roadLoadLinear = model.roadLoad.linear.mean
        roadLoadQuadratic = model.roadLoad.quadratic.mean
    }

    private fun ensureDefaultModelFile() {
        defaultModelFile.parentFile?.mkdirs()

        if (defaultModelFile.exists() && defaultModelFile.length() > 0L) return

        runCatching {
            appContext.assets.open(MODEL_FILE_NAME).use { input ->
                defaultModelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }.onFailure {
            Log.e(TAG, "copy default model failed", it)
        }
    }

    private fun loadModelOrFallback(): VehicleEnergyModel {
        return runCatching {
            instance.createModel(
                defaultModelFile,
                lastBatteryPercent
            )
        }.getOrElse {
            Log.e(TAG, "load model failed, use basic model", it)
            createBasicModel()
        }
    }


    private fun updateEnergyModel(data: EnergyData) {
        val oldModel = mVehicleEnergyModel
        val oldInfo = oldModel.vehicleInfo

        val modelBuilder = oldModel.toBuilder()
        val infoBuilder = oldInfo.toBuilder()

        data.batteryCapacityWh?.let {
            infoBuilder.setBatteryCapacityWh(intUncertainty(it))
        }

        data.auxiliaryDrainWatts?.let {
            lastConsumptionWatts = it
            modelBuilder.setRoadLoad(
                createRoadLoadFromConsumption(it)
            )
        }

        data.batteryPercent?.let { percent ->
            val safePercent = percent.coerceIn(0, 100)
            lastBatteryPercent = safePercent

            val capacityWh = data.batteryCapacityWh
                ?: oldInfo.batteryCapacityWh.mean.takeIf { it > 0 }
                ?: BATTERY_CAPACITY_WH
            infoBuilder.setBatteryLevelWh(
                intUncertainty((capacityWh * safePercent / 100f).toInt())
            )
        }

        data.maximumChargingRateWatts?.let {
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
                if (it) VehicleInfo.HvacStatus.ON_HVAC_STATUS
                else VehicleInfo.HvacStatus.OFF_HVAC_STATUS
            )
        }

        data.auxiliaryDrainWatts?.let { watts ->
            modelBuilder.clearAuxiliaryDrainAtTime()
            modelBuilder.addAuxiliaryDrainAtTime(
                DrainAtTime.newBuilder()
                    .setRelativeTimeSec(0)
                    .setDrainWatts(intUncertainty(watts))
                    .build()
            )
        }

        data.chargingPowerWatts?.let { watts ->
            val maxRate = watts.coerceAtLeast(0)
            infoBuilder.setMaximumChargingRateWatts(maxRate)
        }

        mVehicleEnergyModel = modelBuilder
            .setVehicleInfo(infoBuilder.build())
            .build()

        notifyChange()
    }

    fun updateRealData(data: EnergyData) {
        updateEnergyModel(data)
    }


    private fun notifyChange() {
        appContext.contentResolver?.notifyChange(
            MODEL_URI.toUri(),
            null
        )
    }


    fun createModel(
        defaultFile: File,
        socPercent: Int
    ): VehicleEnergyModel {
        // load model
        val vehicleEnergyModel = VehicleEnergyModel.newBuilder()
        JsonFormat.parser().merge(defaultFile.readText(), vehicleEnergyModel)

        return vehicleEnergyModel.build()
    }

    private fun applyCurrentData(batteryPercent: Int, consumptionWatts: Int): VehicleEnergyModel {
        val vehicleInfo = mVehicleEnergyModel.vehicleInfo
        val batteryWh =
            (vehicleInfo.batteryCapacityWh.mean * batteryPercent.coerceIn(0, 100) / 100f).toInt()

        return mVehicleEnergyModel
            .toBuilder()
            .setVehicleInfo(
                vehicleInfo
                    .toBuilder()
                    .setBatteryLevelWh(intUncertainty(batteryWh))
            )
            .setRoadLoad(
                createRoadLoadFromConsumption(consumptionWatts)
            )
            .build()
    }

    private fun intUncertainty(mean: Int, sd: Float = 0f) =
        IntWithUncertainty.newBuilder()
            .setMean(mean)
            .setStandardDeviation(sd)
            .build()

    private fun floatUncertainty(mean: Float, sd: Float = 0f) =
        FloatWithUncertainty.newBuilder()
            .setMean(mean)
            .setStandardDeviation(sd)
            .build()

    private fun createRoadLoadFromConsumption(whPer100km: Int): RoadLoadForce {
        val scale = (whPer100km.toFloat() / DEFAULT_CONSUMPTION_WH_PER_100KM.toFloat())
            .coerceIn(0.6f, 1.8f)

        return RoadLoadForce.newBuilder()
            .setConstant(floatUncertainty(roadLoadConstant * scale))
            .setLinear(floatUncertainty(roadLoadLinear * scale))
            .setQuadratic(floatUncertainty(roadLoadQuadratic * scale))
            .build()
    }

    private fun createBasicModel(): VehicleEnergyModel {
        return VehicleEnergyModel.newBuilder()
            .setVehicleInfo(
                VehicleInfo.newBuilder()
                    // Atto 3 Extended approx kerb 1,750 kg + passengers/cargo.
                    // Use 1,750 for empty, 2,100–2,160 for real driving gross.
                    .setMassKilograms(intUncertainty(MASS_KG))
                    .setBatteryCapacityWh(intUncertainty(BATTERY_CAPACITY_WH))
                    .setBatteryLevelWh(intUncertainty((BATTERY_CAPACITY_WH * lastBatteryPercent / 100f).toInt()))

                    .setHvacStatus(VehicleInfo.HvacStatus.ON_HVAC_STATUS)
                    .setReferenceAirDensity(1.225f)
                    .setExternalTempCelsius(35f)
                    .setBatteryTempCelsius(35f)
                    .setPeakMotorPowerWatts(PEAK_MOTOR_POWER_WATTS)
                    .setMaximumChargingRateWatts(MAX_DC_CHARGE_WATTS)
                    .setHeatPumpEquipped(true)
                    .build()
            )
            .setRoadLoad(
                createRoadLoadFromConsumption(DEFAULT_CONSUMPTION_WH_PER_100KM)
            )
            .setMaxRegenPower(createMaxRegenPower())
            .setBatteryLosses(createBatteryLosses())
            .setChargingCurves(createAtto3ChargingCurves())
            .addAuxiliaryDrainAtTime(
                DrainAtTime.newBuilder()
                    .setRelativeTimeSec(0)
                    .setDrainWatts(intUncertainty(1_500))
                    .build()
            )
            .build()
    }

    private fun createAtto3ChargingCurves(): ChargingCurves {
        return ChargingCurves.newBuilder()
            .addChargingCurveAtPower(chargingCurveAtPower(7_000, BATTERY_CAPACITY_WH))   // AC 7 kW
            .addChargingCurveAtPower(
                chargingCurveAtPower(
                    22_000,
                    BATTERY_CAPACITY_WH
                )
            )  // public AC, car limits lower if needed
            .addChargingCurveAtPower(chargingCurveAtPower(50_000, BATTERY_CAPACITY_WH))  // DC 50 kW
            .addChargingCurveAtPower(chargingCurveAtPower(88_000, BATTERY_CAPACITY_WH))  // DC max
            .build()
    }

    private fun chargingCurveAtPower(
        inputPowerWatts: Int,
        capacityWh: Int
    ): ChargingCurves.ChargingCurveAtPower {
        return ChargingCurves.ChargingCurveAtPower.newBuilder()
            .setInputPowerWatts(inputPowerWatts)
            .addTimeToFullBattery(timeToFull(0, 0))
            .addTimeToFullBattery(
                timeToFull(
                    (capacityWh * 0.10).toInt(),
                    estimateChargeSecs(inputPowerWatts, capacityWh * 0.10)
                )
            )
            .addTimeToFullBattery(
                timeToFull(
                    (capacityWh * 0.20).toInt(),
                    estimateChargeSecs(inputPowerWatts, capacityWh * 0.20)
                )
            )
            .addTimeToFullBattery(
                timeToFull(
                    (capacityWh * 0.40).toInt(),
                    estimateChargeSecs(inputPowerWatts, capacityWh * 0.40)
                )
            )
            .addTimeToFullBattery(
                timeToFull(
                    (capacityWh * 0.60).toInt(),
                    estimateChargeSecs(inputPowerWatts, capacityWh * 0.60)
                )
            )
            .addTimeToFullBattery(
                timeToFull(
                    (capacityWh * 0.80).toInt(),
                    estimateChargeSecs(inputPowerWatts, capacityWh * 0.80)
                )
            )
            .addTimeToFullBattery(
                timeToFull(
                    capacityWh,
                    estimateChargeSecs(inputPowerWatts, capacityWh.toDouble())
                )
            )
            .build()
    }

    private fun timeToFull(
        energyToFullWh: Int,
        timeToFullSecs: Int
    ): ChargingCurves.ChargingCurveAtPower.TimeToFullBattery {
        return ChargingCurves.ChargingCurveAtPower.TimeToFullBattery.newBuilder()
            .setEnergyToFullWh(energyToFullWh)
            .setTimeToFullSecs(timeToFullSecs)
            .build()
    }

    private fun estimateChargeSecs(inputPowerWatts: Int, energyWh: Double): Int {
        val efficiency = when {
            inputPowerWatts <= 7_000 -> 0.90
            inputPowerWatts <= 22_000 -> 0.90
            inputPowerWatts <= 50_000 -> 0.92
            else -> 0.88
        }

        val effectivePower = when {
            inputPowerWatts <= 7_000 -> inputPowerWatts.toDouble()
            inputPowerWatts <= 22_000 -> 7_000.0 // Atto 3 usually AC limit around 7 kW
            inputPowerWatts <= 50_000 -> 45_000.0
            else -> 58_000.0 // average 10–100%, peak is 88 kW
        }

        return ((energyWh / (effectivePower * efficiency)) * 3600.0).toInt()
    }

    private fun createMaxRegenPower(): MaxRegenPower {
        val b = MaxRegenPower.newBuilder()

        listOf(
            5_000 to 100_000,
            25_000 to 100_000,
            50_000 to 250_000,
            60_000 to 100_000,
            70_000 to 30_000,
            75_000 to 0
        ).forEach { (wh, watts) ->
            b.addRegenerationRateAtEnergyLevel(
                RegenerationRateAtEnergyLevel.newBuilder()
                    .setEnergyLevelWh(wh)
                    .setRegenerationRateWatts(watts)
                    .build()
            )
        }

        return b.build()
    }

    private fun createBatteryLosses(): BatteryLosses {
        val b = BatteryLosses.newBuilder()

        listOf(
            -100_000 to 5_000,
            -50_000 to 2_500,
            -20_000 to 1_000,
            -10_000 to 500,
            0 to 0,
            10_000 to 500,
            20_000 to 1_000,
            50_000 to 2_500,
            100_000 to 5_000,
            200_000 to 10_000,
            400_000 to 20_000,
            600_000 to 30_000
        ).forEach { (power, loss) ->
            b.addBatteryPowerLoss(
                LossAtPower.newBuilder()
                    .setPowerWatts(power)
                    .setLossWatts(intUncertainty(loss))
                    .build()
            )
        }

        return b.build()
    }
}

