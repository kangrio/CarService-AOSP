package android.car.hardware;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;

public final class CarPropertyValue<T> implements Parcelable {
    public static final Creator<CarPropertyValue> CREATOR = new Creator<CarPropertyValue>() {
        public CarPropertyValue createFromParcel(Parcel in) {
            LogTrace.enter("CarPropertyValue", "createFromParcel", "in", in);
            return LogTrace.result("CarPropertyValue", "createFromParcel", new CarPropertyValue(in));
        }

        public CarPropertyValue[] newArray(int size) {
            LogTrace.enter("CarPropertyValue", "newArray", "size", size);
            return LogTrace.result("CarPropertyValue", "newArray", new CarPropertyValue[size]);
        }
    };
    public static final int STATUS_AVAILABLE = 0, STATUS_ERROR = 2, STATUS_UNAVAILABLE = 1;
    private final int propertyId, areaId, status;
    private final long timestamp;
    private final T value;
    public CarPropertyValue() {
        this(0, 0, null);
        LogTrace.enter("CarPropertyValue", "CarPropertyValue");

        LogTrace.voidResult("CarPropertyValue", "CarPropertyValue");
    }

    public CarPropertyValue(int propertyId, int areaId, T value) {
        this(propertyId, areaId, STATUS_AVAILABLE, System.nanoTime(), value);
        LogTrace.enter("CarPropertyValue", "CarPropertyValue", "propertyId", propertyId, "areaId", areaId, "value", value);

        LogTrace.voidResult("CarPropertyValue", "CarPropertyValue");
    }

    public CarPropertyValue(int propertyId, int areaId, int status, long timestamp, T value) {
        LogTrace.enter("CarPropertyValue", "CarPropertyValue", "propertyId", propertyId, "areaId", areaId, "status", status, "timestamp", timestamp, "value", value);
        this.propertyId = propertyId;
        this.areaId = areaId;
        this.status = status;
        this.timestamp = timestamp;
        this.value = value;

        LogTrace.voidResult("CarPropertyValue", "CarPropertyValue");
    }

    private CarPropertyValue(Parcel in) {
        LogTrace.enter("CarPropertyValue", "CarPropertyValue", "in", in);
        propertyId = in.readInt();
        areaId = in.readInt();
        status = in.readInt();
        timestamp = in.readLong();
        value = (T) in.readValue(getClass().getClassLoader());

        LogTrace.voidResult("CarPropertyValue", "CarPropertyValue");
    }

    public int describeContents() {
        LogTrace.enter("CarPropertyValue", "describeContents");
        return LogTrace.result("CarPropertyValue", "describeContents", 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("CarPropertyValue", "writeToParcel", "dest", dest, "flags", flags);
        dest.writeInt(propertyId);
        dest.writeInt(areaId);
        dest.writeInt(status);
        dest.writeLong(timestamp);
        dest.writeValue(value);

        LogTrace.voidResult("CarPropertyValue", "writeToParcel");
    }

    public int getPropertyId() {
        LogTrace.enter("CarPropertyValue", "getPropertyId");
        return LogTrace.result("CarPropertyValue", "getPropertyId", propertyId);
    }

    public int getAreaId() {
        LogTrace.enter("CarPropertyValue", "getAreaId");
        return LogTrace.result("CarPropertyValue", "getAreaId", areaId);
    }

    public int getStatus() {
        LogTrace.enter("CarPropertyValue", "getStatus");
        return LogTrace.result("CarPropertyValue", "getStatus", status);
    }

    public long getTimestamp() {
        LogTrace.enter("CarPropertyValue", "getTimestamp");
        return LogTrace.result("CarPropertyValue", "getTimestamp", timestamp);
    }

    public T getValue() {
        LogTrace.enter("CarPropertyValue", "getValue");
        return LogTrace.result("CarPropertyValue", "getValue", value);
    }

    public String toString() {
        LogTrace.enter("CarPropertyValue", "toString");
        return LogTrace.result("CarPropertyValue", "toString", "CarPropertyValue{" + propertyId + "," + areaId + "," + value + "}");
    }

    public interface PropertyStatus {
    }
}
