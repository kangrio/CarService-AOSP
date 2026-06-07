package android.car.hardware;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class CarPropertyConfig<T> implements Parcelable {
    public static final Creator<CarPropertyConfig> CREATOR = new Creator<CarPropertyConfig>() {
        public CarPropertyConfig createFromParcel(Parcel in) {
            LogTrace.enter("CarPropertyConfig", "createFromParcel", "in", in);
            return LogTrace.result("CarPropertyConfig", "createFromParcel", new CarPropertyConfig(in.readInt(), Object.class));
        }

        public CarPropertyConfig[] newArray(int s) {
            LogTrace.enter("CarPropertyConfig", "newArray", "s", s);
            return LogTrace.result("CarPropertyConfig", "newArray", new CarPropertyConfig[s]);
        }
    };
    public static final int VEHICLE_PROPERTY_ACCESS_NONE = 0, VEHICLE_PROPERTY_ACCESS_READ = 1, VEHICLE_PROPERTY_ACCESS_READ_WRITE = 3, VEHICLE_PROPERTY_ACCESS_WRITE = 2, VEHICLE_PROPERTY_CHANGE_MODE_CONTINUOUS = 2, VEHICLE_PROPERTY_CHANGE_MODE_ONCHANGE = 1, VEHICLE_PROPERTY_CHANGE_MODE_STATIC = 0;
    private final int propertyId;
    private final Class<T> type;
    private int access = VEHICLE_PROPERTY_ACCESS_READ_WRITE, areaType = 0, changeMode = VEHICLE_PROPERTY_CHANGE_MODE_ONCHANGE;
    private float minRate = 0, maxRate = 100;
    private T minValue, maxValue;
    private int[] areaIds = new int[]{0};
    private List<Integer> configArray = new ArrayList<>();

    public CarPropertyConfig() {
        this(0, (Class<T>) Object.class);
        LogTrace.enter("CarPropertyConfig", "CarPropertyConfig");

        LogTrace.voidResult("CarPropertyConfig", "CarPropertyConfig");
    }

    public CarPropertyConfig(int propertyId, Class<T> type) {
        LogTrace.enter("CarPropertyConfig", "CarPropertyConfig", "propertyId", propertyId, "type", type);
        Log.w("CarPropertyConfig", "CarPropertyConfig: " + propertyId);
        this.propertyId = propertyId;
        this.type = type;

        LogTrace.voidResult("CarPropertyConfig", "CarPropertyConfig");
    }

    public int getAccess() {
        LogTrace.enter("CarPropertyConfig", "getAccess");
        Log.w("CarPropertyConfig", "getAccess");
        return LogTrace.result("CarPropertyConfig", "getAccess", access);
    }

    public int getAreaType() {
        LogTrace.enter("CarPropertyConfig", "getAreaType");
        Log.w("CarPropertyConfig", "getAreaType");
        return LogTrace.result("CarPropertyConfig", "getAreaType", areaType);
    }

    public int getChangeMode() {
        LogTrace.enter("CarPropertyConfig", "getChangeMode");
        Log.w("CarPropertyConfig", "getChangeMode");
        return LogTrace.result("CarPropertyConfig", "getChangeMode", changeMode);
    }

    public List<Integer> getConfigArray() {
        LogTrace.enter("CarPropertyConfig", "getConfigArray");
        Log.w("CarPropertyConfig", "getConfigArray");
        return LogTrace.result("CarPropertyConfig", "getConfigArray", configArray);
    }

    public float getMaxSampleRate() {
        LogTrace.enter("CarPropertyConfig", "getMaxSampleRate");
        Log.w("CarPropertyConfig", "getMaxSampleRate");
        return LogTrace.result("CarPropertyConfig", "getMaxSampleRate", maxRate);
    }

    public float getMinSampleRate() {
        LogTrace.enter("CarPropertyConfig", "getMinSampleRate");
        Log.w("CarPropertyConfig", "getMinSampleRate");
        return LogTrace.result("CarPropertyConfig", "getMinSampleRate", minRate);
    }

    public int getPropertyId() {
        LogTrace.enter("CarPropertyConfig", "getPropertyId");
        Log.w("CarPropertyConfig", "getPropertyId");
        return LogTrace.result("CarPropertyConfig", "getPropertyId", propertyId);
    }

    public Class<T> getPropertyType() {
        LogTrace.enter("CarPropertyConfig", "getPropertyType");
        Log.w("CarPropertyConfig", "getPropertyType");
        return LogTrace.result("CarPropertyConfig", "getPropertyType", type);
    }

    public boolean isGlobalProperty() {
        LogTrace.enter("CarPropertyConfig", "isGlobalProperty");
        Log.w("CarPropertyConfig", "isGlobalProperty");
        return LogTrace.result("CarPropertyConfig", "isGlobalProperty", areaIds.length == 1 && areaIds[0] == 0);
    }

    public int[] getAreaIds() {
        LogTrace.enter("CarPropertyConfig", "getAreaIds");
        Log.w("CarPropertyConfig", "getAreaIds");
        return LogTrace.result("CarPropertyConfig", "getAreaIds", areaIds);
    }

    public T getMinValue(int areaId) {
        LogTrace.enter("CarPropertyConfig", "getMinValue", "areaId", areaId);
        Log.w("CarPropertyConfig", "getMinValue: areaId: " + areaId);
        return LogTrace.result("CarPropertyConfig", "getMinValue", minValue);
    }

    public T getMaxValue(int areaId) {
        LogTrace.enter("CarPropertyConfig", "getMaxValue", "areaId", areaId);
        Log.w("CarPropertyConfig", "getMaxValue: areaId: " + areaId);
        return LogTrace.result("CarPropertyConfig", "getMaxValue", maxValue);
    }

    public T getMinValue() {
        LogTrace.enter("CarPropertyConfig", "getMinValue");
        Log.w("CarPropertyConfig", "getMinValue");
        return LogTrace.result("CarPropertyConfig", "getMinValue", minValue);
    }

    public T getMaxValue() {
        LogTrace.enter("CarPropertyConfig", "getMaxValue");
        Log.w("CarPropertyConfig", "getMaxValue");
        return LogTrace.result("CarPropertyConfig", "getMaxValue", maxValue);
    }

    public int describeContents() {
        LogTrace.enter("CarPropertyConfig", "describeContents");
        Log.w("CarPropertyConfig", "describeContents");
        return LogTrace.result("CarPropertyConfig", "describeContents", 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("CarPropertyConfig", "writeToParcel", "dest", dest, "flags", flags);
        Log.w("CarPropertyConfig", "writeToParcel: dest: " + dest + " flags: " + flags);
        dest.writeInt(propertyId);

        LogTrace.voidResult("CarPropertyConfig", "writeToParcel");
    }

    public String toString() {
        LogTrace.enter("CarPropertyConfig", "toString");
        return LogTrace.result("CarPropertyConfig", "toString", "CarPropertyConfig{" + propertyId + "}");
    }
}
