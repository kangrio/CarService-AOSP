package com.byd.car;


import androidx.annotation.NonNull;

import com.byd.foundation.utils.LogUtils;
import com.google.maps.automotive.proto.BatteryLosses;
import com.google.maps.automotive.proto.ChargingCurves;
import com.google.maps.automotive.proto.DrainAtTemperature;
import com.google.maps.automotive.proto.DrainAtTime;
import com.google.maps.automotive.proto.DrivetrainLosses;
import com.google.maps.automotive.proto.IntWithUncertainty;
import com.google.maps.automotive.proto.MaxRegenPower;
import com.google.maps.automotive.proto.RoadLoadForce;
import com.google.maps.automotive.proto.VehicleEnergyModel;
import com.google.maps.automotive.proto.VehicleInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VEMData implements Serializable {
    private static final long serialVersionUID = 1;
    public ChargingCurves mChargingCurves;
    public List<DrainAtTemperature> mDrainAtTemperatureList;
    public DrivetrainLosses mDrivetrainLosses;
    public RoadLoadForce mRoadLoad;
    public VehicleInfo mVehicleInfo;
    public Map<Integer, MaxRegenPower> mMaxRegenPowerMap = new HashMap();
    public Map<Integer, List<DrainAtTime>> mDrainAtTimeMap = new HashMap();
    public Map<Integer, BatteryLosses> mBatteryLossesMap = new HashMap();

    @NonNull
    @Override
    public String toString() {
        return "mChargingCurves=" + mChargingCurves.toString() + "\n\n" + "mDrainAtTemperatureList=" + mDrainAtTemperatureList.toString() + "\n\n" +
                "mDrivetrainLosses=" + mDrivetrainLosses.toString() + "\n\n" + "mRoadLoad=" + mRoadLoad.toString() + "\n\n" +
                "mVehicleInfo=" + mVehicleInfo.toString() + "\n\n" + "mMaxRegenPowerMap=" + mMaxRegenPowerMap.toString() + "\n\n" +
                "mDrainAtTimeMap=" + mDrainAtTimeMap.toString() + "\n\n" + "mBatteryLossesMap=" + mBatteryLossesMap.toString() + "\n\n";
    }

    private <T> T getDataByTemp(int temp, Map<Integer, T> map, AtomicInteger lastKeyTemp) {
        List list = (List) map.keySet().stream().sorted().collect(Collectors.toList());
        Integer numValueOf = -9999;
        int i = 0;
        if (temp <= ((Integer) list.get(0)).intValue()) {
            numValueOf = (Integer) list.get(0);
        }
        int size = list.size() - 1;
        if (numValueOf.intValue() == -9999 && temp >= ((Integer) list.get(size)).intValue()) {
            numValueOf = (Integer) list.get(size);
        }
        if (numValueOf.intValue() == -9999 && list.size() >= 2) {
            while (i < size) {
                int iIntValue = ((Integer) list.get(i)).intValue();
                i++;
                int iIntValue2 = ((Integer) list.get(i)).intValue();
                if (temp >= iIntValue && temp <= iIntValue2) {
                    if (temp >= (iIntValue + iIntValue2) / 2) {
                        iIntValue = iIntValue2;
                    }
                    numValueOf = Integer.valueOf(iIntValue);
                }
            }
        }
        if (lastKeyTemp.get() == numValueOf.intValue()) {
            return null;
        }
        lastKeyTemp.set(numValueOf.intValue());
        return map.get(numValueOf);
    }

    public BatteryLosses getBatteryLossesByTemp(int temp, AtomicInteger mBatteryLossesTemp) {
        BatteryLosses batteryLosses = (BatteryLosses) getDataByTemp(temp, this.mBatteryLossesMap, mBatteryLossesTemp);
        if (batteryLosses != null) {
            LogUtils.d("VehicleEnergyModelManager", "BatteryLosses Updated, New Key is " + mBatteryLossesTemp.toString());
        }
        return batteryLosses;
    }

    public List<DrainAtTime> getDrainAtTimeByTemp(int temp, AtomicInteger mDrainAtTimeTemp) {
        List<DrainAtTime> list = (List) getDataByTemp(temp, this.mDrainAtTimeMap, mDrainAtTimeTemp);
        if (list != null) {
            LogUtils.d("VehicleEnergyModelManager", "DrainAtTime Updated, New Key is " + mDrainAtTimeTemp.toString());
        }
        return list;
    }

    public MaxRegenPower getMaxRegenPowerByTemp(int temp, AtomicInteger mMaxRegenPowerTemp) {
        MaxRegenPower maxRegenPower = (MaxRegenPower) getDataByTemp(temp, this.mMaxRegenPowerMap, mMaxRegenPowerTemp);
        if (maxRegenPower != null) {
            LogUtils.d("VehicleEnergyModelManager", "MaxRegenPower Updated, New Key is " + mMaxRegenPowerTemp.toString());
        }
        return maxRegenPower;
    }

    public VehicleEnergyModel getVehicleEnergyModel(int externalTemp, int batteryTemp, int batteryLevelWh, AtomicInteger mBatteryLossesTemp, AtomicInteger mDrainAtTimeTemp, AtomicInteger mMaxRegenPowerTemp) {
        VehicleEnergyModel.Builder builderNewBuilder = VehicleEnergyModel.newBuilder();
        builderNewBuilder.setVehicleInfo(this.mVehicleInfo.toBuilder().setExternalTempCelsius(externalTemp).setBatteryLevelWh(IntWithUncertainty.newBuilder().setMean(batteryLevelWh).build()).build());
        builderNewBuilder.setRoadLoad(this.mRoadLoad);
        builderNewBuilder.setDrivetrainLosses(this.mDrivetrainLosses);
        builderNewBuilder.addAllAuxiliaryDrainAtTemp(this.mDrainAtTemperatureList);
        builderNewBuilder.setChargingCurves(this.mChargingCurves);
        MaxRegenPower maxRegenPowerByTemp = getMaxRegenPowerByTemp(externalTemp, mMaxRegenPowerTemp);
        List<DrainAtTime> drainAtTimeByTemp = getDrainAtTimeByTemp(externalTemp, mDrainAtTimeTemp);
        BatteryLosses batteryLossesByTemp = getBatteryLossesByTemp(batteryTemp, mBatteryLossesTemp);
        if (maxRegenPowerByTemp != null) {
            builderNewBuilder.setMaxRegenPower(maxRegenPowerByTemp);
        }
        if (drainAtTimeByTemp != null) {
            builderNewBuilder.addAllAuxiliaryDrainAtTime(drainAtTimeByTemp);
        }
        if (batteryLossesByTemp != null) {
            builderNewBuilder.setBatteryLosses(batteryLossesByTemp);
        }
        return builderNewBuilder.build();
    }
}
