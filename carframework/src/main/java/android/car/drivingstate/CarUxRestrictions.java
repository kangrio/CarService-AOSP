package android.car.drivingstate;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;

public final class CarUxRestrictions implements Parcelable {

    public static final int UX_RESTRICTIONS_BASELINE = 0;
    public static final int UX_RESTRICTIONS_FULLY_RESTRICTED = 511;
    public static final int UX_RESTRICTIONS_LIMIT_CONTENT = 32;
    public static final int UX_RESTRICTIONS_LIMIT_STRING_LENGTH = 4;
    public static final int UX_RESTRICTIONS_NO_DIALPAD = 1;
    public static final int UX_RESTRICTIONS_NO_FILTERING = 2;
    public static final int UX_RESTRICTIONS_NO_KEYBOARD = 8;
    public static final int UX_RESTRICTIONS_NO_SETUP = 64;
    public static final int UX_RESTRICTIONS_NO_TEXT_MESSAGE = 128;
    public static final int UX_RESTRICTIONS_NO_VIDEO = 16;
    public static final int UX_RESTRICTIONS_NO_VOICE_TRANSCRIPTION = 256;
    public static final Creator<CarUxRestrictions> CREATOR = new Creator<CarUxRestrictions>() {
        public CarUxRestrictions createFromParcel(Parcel in) {
            LogTrace.enter("CarUxRestrictions", "createFromParcel", "in", in);
            return LogTrace.result("CarUxRestrictions", "createFromParcel", new CarUxRestrictions(in));
        }

        public CarUxRestrictions[] newArray(int size) {
            LogTrace.enter("CarUxRestrictions", "newArray", "size", size);
            return LogTrace.result("CarUxRestrictions", "newArray", new CarUxRestrictions[size]);
        }
    };
    private final boolean req;
    private final int restrictions, maxStr, maxItems, maxDepth;
    private final long time;

    public CarUxRestrictions() {
        this(false, 0, 0, 0, 0, 0);
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions");
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    public CarUxRestrictions(boolean req, int restrictions) {
        this(req, restrictions, 0, 0, 0, System.currentTimeMillis());
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions", "req", req, "restrictions", restrictions);
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    public CarUxRestrictions(boolean req, int restrictions, int maxStr, int maxItems, int maxDepth) {
        this(req, restrictions, maxStr, maxItems, maxDepth, System.currentTimeMillis());
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions", "req", req, "restrictions", restrictions, "maxStr", maxStr, "maxItems", maxItems, "maxDepth", maxDepth);
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    public CarUxRestrictions(boolean req, int restrictions, int maxStr, int maxItems, int maxDepth, long time) {
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions", "req", req, "restrictions", restrictions, "maxStr", maxStr, "maxItems", maxItems, "maxDepth", maxDepth, "time", time);
        this.req = req;
        this.restrictions = restrictions;
        this.maxStr = maxStr;
        this.maxItems = maxItems;
        this.maxDepth = maxDepth;
        this.time = time;
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    public CarUxRestrictions(CarUxRestrictions u) {
        this(u != null && u.req, u != null ? u.restrictions : 0, u != null ? u.maxStr : 0, u != null ? u.maxItems : 0, u != null ? u.maxDepth : 0, u != null ? u.time : System.currentTimeMillis());
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions", "u", u);
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    private CarUxRestrictions(Parcel in) {
        LogTrace.enter("CarUxRestrictions", "CarUxRestrictions", "in", in);
        req = in.readInt() != 0;
        restrictions = in.readInt();
        maxStr = in.readInt();
        maxItems = in.readInt();
        maxDepth = in.readInt();
        time = in.readLong();
        LogTrace.voidResult("CarUxRestrictions", "CarUxRestrictions");
    }

    public boolean isRequiresDistractionOptimization() {
        LogTrace.enter("CarUxRestrictions", "isRequiresDistractionOptimization");
        return LogTrace.result("CarUxRestrictions", "isRequiresDistractionOptimization", req);
    }

    public int getActiveRestrictions() {
        LogTrace.enter("CarUxRestrictions", "getActiveRestrictions");
        return LogTrace.result("CarUxRestrictions", "getActiveRestrictions", restrictions);
    }

    public int getMaxRestrictedStringLength() {
        LogTrace.enter("CarUxRestrictions", "getMaxRestrictedStringLength");
        return LogTrace.result("CarUxRestrictions", "getMaxRestrictedStringLength", maxStr);
    }

    public int getMaxCumulativeContentItems() {
        LogTrace.enter("CarUxRestrictions", "getMaxCumulativeContentItems");
        return LogTrace.result("CarUxRestrictions", "getMaxCumulativeContentItems", maxItems);
    }

    public int getMaxContentDepth() {
        LogTrace.enter("CarUxRestrictions", "getMaxContentDepth");
        return LogTrace.result("CarUxRestrictions", "getMaxContentDepth", maxDepth);
    }

    public long getTimeStamp() {
        LogTrace.enter("CarUxRestrictions", "getTimeStamp");
        return LogTrace.result("CarUxRestrictions", "getTimeStamp", time);
    }

    public int describeContents() {
        LogTrace.enter("CarUxRestrictions", "describeContents");
        return LogTrace.result("CarUxRestrictions", "describeContents", 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("CarUxRestrictions", "writeToParcel", "dest", dest, "flags", flags);
        dest.writeInt(req ? 1 : 0);
        dest.writeInt(restrictions);
        dest.writeInt(maxStr);
        dest.writeInt(maxItems);
        dest.writeInt(maxDepth);
        dest.writeLong(time);
        LogTrace.voidResult("CarUxRestrictions", "writeToParcel");
    }

    public boolean isSameRestrictions(CarUxRestrictions o) {
        LogTrace.enter("CarUxRestrictions", "isSameRestrictions", "o", o);
        return LogTrace.result("CarUxRestrictions", "isSameRestrictions", o != null && req == o.req && restrictions == o.restrictions && maxStr == o.maxStr && maxItems == o.maxItems && maxDepth == o.maxDepth);
    }

    public String toString() {
        LogTrace.enter("CarUxRestrictions", "toString");
        return LogTrace.result("CarUxRestrictions", "toString", "CarUxRestrictions{" + "req=" + req + ", restrictions=" + restrictions + ", maxStr=" + maxStr + ", maxItems=" + maxItems + ", maxDepth=" + maxDepth + "}");
    }

    public interface CarUxRestrictionsInfo {
    }

    class Builder {
        private final boolean req;
        private final int restrictions;
        private final long time;
        private int maxStringLength, maxCumulativeContentItems, maxContentDepth;

        public Builder(boolean reqOpt, int restrictions, long time) {
            LogTrace.enter("CarUxRestrictions", "Builder", "reqOpt", reqOpt, "restrictions", restrictions, "time", time);
            this.req = reqOpt;
            this.restrictions = restrictions;
            this.time = time;
        }

        public Builder setMaxStringLength(int length) {
            LogTrace.enter("CarUxRestrictions", "setMaxStringLength", "length", length);
            maxStringLength = Math.max(0, length);
            return LogTrace.result("CarUxRestrictions", "setMaxStringLength", this);
        }

        public Builder setMaxCumulativeContentItems(int number) {
            LogTrace.enter("CarUxRestrictions", "setMaxCumulativeContentItems", "number", number);
            maxCumulativeContentItems = Math.max(0, number);
            return LogTrace.result("CarUxRestrictions", "setMaxCumulativeContentItems", this);
        }

        public Builder setMaxContentDepth(int depth) {
            LogTrace.enter("CarUxRestrictions", "setMaxContentDepth", "depth", depth);
            maxContentDepth = Math.max(0, depth);
            return LogTrace.result("CarUxRestrictions", "setMaxContentDepth", this);
        }

        public CarUxRestrictions build() {
            LogTrace.enter("CarUxRestrictions", "build");
            return LogTrace.result("CarUxRestrictions", "build", new CarUxRestrictions(req, restrictions, maxStringLength, maxCumulativeContentItems, maxContentDepth, time));
        }
    }
}
