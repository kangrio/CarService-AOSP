package android.car.settings;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;

@Deprecated
public final class SpeedBumpConfiguration implements Parcelable {
    @Deprecated
    public static final Creator<SpeedBumpConfiguration> CREATOR = new Creator<SpeedBumpConfiguration>() {
        public SpeedBumpConfiguration createFromParcel(Parcel in) {
            LogTrace.enter("SpeedBumpConfiguration", "createFromParcel", "in", in);
            return LogTrace.result("SpeedBumpConfiguration", "createFromParcel", new SpeedBumpConfiguration(in.readDouble(), in.readDouble(), in.readLong()));
        }

        public SpeedBumpConfiguration[] newArray(int size) {
            LogTrace.enter("SpeedBumpConfiguration", "newArray", "size", size);
            return LogTrace.result("SpeedBumpConfiguration", "newArray", new SpeedBumpConfiguration[size]);
        }
    };
    private final double permitsPerSecond, maxPermitPool;
    private final long permitFillDelay;

    @Deprecated
    public SpeedBumpConfiguration(double permitsPerSecond, double maxPermitPool, long permitFillDelay) {
        LogTrace.enter("SpeedBumpConfiguration", "SpeedBumpConfiguration", "permitsPerSecond", permitsPerSecond, "maxPermitPool", maxPermitPool, "permitFillDelay", permitFillDelay);
        this.permitsPerSecond = permitsPerSecond;
        this.maxPermitPool = maxPermitPool;
        this.permitFillDelay = permitFillDelay;
        LogTrace.voidResult("SpeedBumpConfiguration", "SpeedBumpConfiguration");
    }

    @Deprecated
    public double getAcquiredPermitsPerSecond() {
        LogTrace.enter("SpeedBumpConfiguration", "getAcquiredPermitsPerSecond");
        return LogTrace.result("SpeedBumpConfiguration", "getAcquiredPermitsPerSecond", permitsPerSecond);
    }

    @Deprecated
    public double getMaxPermitPool() {
        LogTrace.enter("SpeedBumpConfiguration", "getMaxPermitPool");
        return LogTrace.result("SpeedBumpConfiguration", "getMaxPermitPool", maxPermitPool);
    }

    @Deprecated
    public long getPermitFillDelay() {
        LogTrace.enter("SpeedBumpConfiguration", "getPermitFillDelay");
        return LogTrace.result("SpeedBumpConfiguration", "getPermitFillDelay", permitFillDelay);
    }

    @Deprecated
    public String toString() {
        LogTrace.enter("SpeedBumpConfiguration", "toString");
        return LogTrace.result("SpeedBumpConfiguration", "toString", "SpeedBumpConfiguration{" + permitsPerSecond + "," + maxPermitPool + "," + permitFillDelay + "}");
    }

    @Deprecated
    public boolean equals(Object o) {
        LogTrace.enter("SpeedBumpConfiguration", "equals", "o", o);
        if (!(o instanceof SpeedBumpConfiguration))
            return LogTrace.result("SpeedBumpConfiguration", "equals", false);
        SpeedBumpConfiguration s = (SpeedBumpConfiguration) o;
        return LogTrace.result("SpeedBumpConfiguration", "equals", permitsPerSecond == s.permitsPerSecond && maxPermitPool == s.maxPermitPool && permitFillDelay == s.permitFillDelay);
    }

    @Deprecated
    public int hashCode() {
        LogTrace.enter("SpeedBumpConfiguration", "hashCode");
        return LogTrace.result("SpeedBumpConfiguration", "hashCode", java.util.Objects.hash(permitsPerSecond, maxPermitPool, permitFillDelay));
    }

    @Deprecated
    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("SpeedBumpConfiguration", "writeToParcel", "dest", dest, "flags", flags);
        dest.writeDouble(permitsPerSecond);
        dest.writeDouble(maxPermitPool);
        dest.writeLong(permitFillDelay);
        LogTrace.voidResult("SpeedBumpConfiguration", "writeToParcel");
    }

    @Deprecated
    public int describeContents() {
        LogTrace.enter("SpeedBumpConfiguration", "describeContents");
        return LogTrace.result("SpeedBumpConfiguration", "describeContents", 0);
    }
}
