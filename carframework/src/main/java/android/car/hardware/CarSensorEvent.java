package android.car.hardware;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

@Deprecated
public class CarSensorEvent implements Parcelable {

    @Deprecated
    public static final Creator<CarSensorEvent> CREATOR = new Creator<CarSensorEvent>() {
        public CarSensorEvent createFromParcel(Parcel in) {
            LogTrace.enter("CarSensorEvent", "createFromParcel", "in", in);
            return LogTrace.result("CarSensorEvent", "createFromParcel", new CarSensorEvent(in));
        }

        public CarSensorEvent[] newArray(int size) {
            LogTrace.enter("CarSensorEvent", "newArray", "size", size);
            return LogTrace.result("CarSensorEvent", "newArray", new CarSensorEvent[size]);
        }
    };
    @Deprecated
    public static final int GEAR_DRIVE = 8, GEAR_EIGHTH = 2048, GEAR_FIFTH = 256, GEAR_FIRST = 16, GEAR_FOURTH = 128, GEAR_NEUTRAL = 1, GEAR_NINTH = 4096, GEAR_PARK = 4, GEAR_REVERSE = 2, GEAR_SECOND = 32, GEAR_SEVENTH = 1024, GEAR_SIXTH = 512, GEAR_TENTH = 8192, GEAR_THIRD = 64, IGNITION_STATE_ACC = 3, IGNITION_STATE_LOCK = 1, IGNITION_STATE_OFF = 2, IGNITION_STATE_ON = 4, IGNITION_STATE_START = 5, IGNITION_STATE_UNDEFINED = 0, INDEX_ENVIRONMENT_TEMPERATURE = 0, INDEX_WHEEL_DISTANCE_FRONT_LEFT = 1, INDEX_WHEEL_DISTANCE_FRONT_RIGHT = 2, INDEX_WHEEL_DISTANCE_REAR_LEFT = 4, INDEX_WHEEL_DISTANCE_REAR_RIGHT = 3, INDEX_WHEEL_DISTANCE_RESET_COUNT = 0;
    @Deprecated
    public final float[] floatValues;
    @Deprecated
    public final int[] intValues;
    @Deprecated
    public final long[] longValues;
    @Deprecated
    public int sensorType;
    @Deprecated
    public long timestamp;
    public CarSensorEvent() {
        this(0, new float[]{0.0f}, new int[]{1}, new long[]{1});
        LogTrace.enter("CarSensorEvent", "CarSensorEvent");
        LogTrace.voidResult("CarSensorEvent", "CarSensorEvent");
    }

    public CarSensorEvent(int type, float[] f) {
        this(type, f, new int[]{1}, new long[]{1});
        LogTrace.enter("CarSensorEvent", "CarSensorEvent", "type", type, "f", f);
        LogTrace.voidResult("CarSensorEvent", "CarSensorEvent");
    }

    public CarSensorEvent(int type, float[] f, int[] i, long[] l) {
        LogTrace.enter("CarSensorEvent", "CarSensorEvent", "type", type, "f", f, "i", i, "l", l);
        sensorType = type;
        floatValues = f == null ? new float[0] : f;
        intValues = i == null ? new int[0] : i;
        longValues = l == null ? new long[0] : l;
        timestamp = System.nanoTime();

        LogTrace.voidResult("CarSensorEvent", "CarSensorEvent");
    }

    private CarSensorEvent(Parcel in) {
        LogTrace.enter("CarSensorEvent", "CarSensorEvent", "in", in);
        sensorType = in.readInt();
        timestamp = in.readLong();
        floatValues = in.createFloatArray();
        intValues = in.createIntArray();
        longValues = in.createLongArray();

        LogTrace.voidResult("CarSensorEvent", "CarSensorEvent");
    }

    @Deprecated
    public int describeContents() {
        LogTrace.enter("CarSensorEvent", "describeContents");
        return LogTrace.result("CarSensorEvent", "describeContents", 0);
    }

    @Deprecated
    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("CarSensorEvent", "writeToParcel", "dest", dest, "flags", flags);
        dest.writeInt(sensorType);
        dest.writeLong(timestamp);
        dest.writeFloatArray(floatValues);
        dest.writeIntArray(intValues);
        dest.writeLongArray(longValues);

        LogTrace.voidResult("CarSensorEvent", "writeToParcel");
    }

    public String toString() {
        LogTrace.enter("CarSensorEvent", "toString");
        return LogTrace.result("CarSensorEvent", "toString", "CarSensorEvent{" + sensorType + ", " + Arrays.toString(floatValues) + "}");
    }

    @Deprecated
    public class EnvironmentData {
        @Deprecated
        public float temperature;
        @Deprecated
        public long timestamp;

        public EnvironmentData() {
            this(25f, System.nanoTime());
            LogTrace.enter("CarSensorEvent", "EnvironmentData");
            LogTrace.voidResult("CarSensorEvent", "EnvironmentData");
        }

        public EnvironmentData(float temperature, long timestamp) {
            LogTrace.enter("CarSensorEvent", "EnvironmentData", "temperature", temperature, "timestamp", timestamp);
            this.temperature = temperature;
            this.timestamp = timestamp;
            LogTrace.voidResult("CarSensorEvent", "EnvironmentData");
        }
    }
}
