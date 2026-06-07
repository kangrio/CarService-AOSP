package android.car;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CarOccupantZoneManager {
    public static final int DISPLAY_TYPE_AUXILIARY = 5;
    public static final int DISPLAY_TYPE_HUD = 3;
    public static final int DISPLAY_TYPE_INPUT = 4;
    public static final int DISPLAY_TYPE_INSTRUMENT_CLUSTER = 2;
    public static final int DISPLAY_TYPE_MAIN = 1;
    public static final int DISPLAY_TYPE_UNKNOWN = 0;
    public static final int OCCUPANT_TYPE_DRIVER = 0;
    public static final int OCCUPANT_TYPE_FRONT_PASSENGER = 1;
    public static final int OCCUPANT_TYPE_REAR_PASSENGER = 2;
    public static final int ZONE_CONFIG_CHANGE_FLAG_AUDIO = 4;
    public static final int ZONE_CONFIG_CHANGE_FLAG_DISPLAY = 1;
    public static final int ZONE_CONFIG_CHANGE_FLAG_USER = 2;
    private final CopyOnWriteArrayList<OccupantZoneConfigChangeListener> listeners = new CopyOnWriteArrayList<>();


    public CarOccupantZoneManager() {
        LogTrace.enter("CarOccupantZoneManager", "CarOccupantZoneManager");

        LogTrace.voidResult("CarOccupantZoneManager", "CarOccupantZoneManager");
    }

    public List<OccupantZoneInfo> getAllOccupantZones() {
        LogTrace.enter("CarOccupantZoneManager", "getAllOccupantZones");
        return LogTrace.result("CarOccupantZoneManager", "getAllOccupantZones", Collections.singletonList(new OccupantZoneInfo()));
    }

    public List<Display> getAllDisplaysForOccupant(OccupantZoneInfo occupantZone) {
        LogTrace.enter("CarOccupantZoneManager", "getAllDisplaysForOccupant", "occupantZone", occupantZone);
        return LogTrace.result("CarOccupantZoneManager", "getAllDisplaysForOccupant", new ArrayList<>());
    }

    public Display getDisplayForOccupant(OccupantZoneInfo occupantZone, int displayType) {
        LogTrace.enter("CarOccupantZoneManager", "getDisplayForOccupant", "occupantZone", occupantZone, "displayType", displayType);
        return LogTrace.result("CarOccupantZoneManager", "getDisplayForOccupant", null);
    }

    public int getDisplayType(Display display) {
        LogTrace.enter("CarOccupantZoneManager", "getDisplayType", "display", display);
        return LogTrace.result("CarOccupantZoneManager", "getDisplayType", DISPLAY_TYPE_MAIN);
    }

    public int getUserForOccupant(OccupantZoneInfo occupantZone) {
        LogTrace.enter("CarOccupantZoneManager", "getUserForOccupant", "occupantZone", occupantZone);
        return LogTrace.result("CarOccupantZoneManager", "getUserForOccupant", 0);
    }

    public void registerOccupantZoneConfigChangeListener(OccupantZoneConfigChangeListener listener) {
        LogTrace.enter("CarOccupantZoneManager", "registerOccupantZoneConfigChangeListener", "listener", listener);
        if (listener != null) listeners.addIfAbsent(listener);

        LogTrace.voidResult("CarOccupantZoneManager", "registerOccupantZoneConfigChangeListener");
    }

    public void unregisterOccupantZoneConfigChangeListener(OccupantZoneConfigChangeListener listener) {
        LogTrace.enter("CarOccupantZoneManager", "unregisterOccupantZoneConfigChangeListener", "listener", listener);
        listeners.remove(listener);

        LogTrace.voidResult("CarOccupantZoneManager", "unregisterOccupantZoneConfigChangeListener");
    }

    public interface OccupantZoneConfigChangeListener {
        void onOccupantZoneConfigChanged(int i);
    }

    public static final class OccupantZoneInfo implements Parcelable {
        public static final Creator<OccupantZoneInfo> CREATOR = new Creator<OccupantZoneInfo>() {
            public OccupantZoneInfo createFromParcel(Parcel in) {
                LogTrace.enter("CarOccupantZoneManager", "createFromParcel", "in", in);
                return LogTrace.result("CarOccupantZoneManager", "createFromParcel", new OccupantZoneInfo(in));
            }

            public OccupantZoneInfo[] newArray(int s) {
                LogTrace.enter("CarOccupantZoneManager", "newArray", "s", s);
                return LogTrace.result("CarOccupantZoneManager", "newArray", new OccupantZoneInfo[s]);
            }
        };


        public final int occupantType;
        public final int seat;
        public int zoneId;

        public OccupantZoneInfo() {
            this(0, CarOccupantZoneManager.OCCUPANT_TYPE_DRIVER, VehicleAreaSeat.SEAT_ROW_1_LEFT);
            LogTrace.enter("CarOccupantZoneManager", "OccupantZoneInfo");
            LogTrace.voidResult("CarOccupantZoneManager", "OccupantZoneInfo");
        }

        public OccupantZoneInfo(int zoneId, int occupantType, int seat) {
            LogTrace.enter("CarOccupantZoneManager", "OccupantZoneInfo", "zoneId", zoneId, "occupantType", occupantType, "seat", seat);
            this.zoneId = zoneId;
            this.occupantType = occupantType;
            this.seat = seat;
            LogTrace.voidResult("CarOccupantZoneManager", "OccupantZoneInfo");
        }

        private OccupantZoneInfo(Parcel in) {
            LogTrace.enter("CarOccupantZoneManager", "OccupantZoneInfo", "in", in);
            zoneId = in.readInt();
            occupantType = in.readInt();
            seat = in.readInt();
            LogTrace.voidResult("CarOccupantZoneManager", "OccupantZoneInfo");
        }

        public int describeContents() {
            LogTrace.enter("CarOccupantZoneManager", "describeContents");
            return LogTrace.result("CarOccupantZoneManager", "describeContents", 0);
        }

        public void writeToParcel(Parcel dest, int flags) {
            LogTrace.enter("CarOccupantZoneManager", "writeToParcel", "dest", dest, "flags", flags);
            dest.writeInt(zoneId);
            dest.writeInt(occupantType);
            dest.writeInt(seat);

            LogTrace.voidResult("CarOccupantZoneManager", "writeToParcel");
        }

        public boolean equals(Object o) {
            LogTrace.enter("CarOccupantZoneManager", "equals", "o", o);
            if (!(o instanceof OccupantZoneInfo))
                return LogTrace.result("CarOccupantZoneManager", "equals", false);
            OccupantZoneInfo z = (OccupantZoneInfo) o;
            return LogTrace.result("CarOccupantZoneManager", "equals", zoneId == z.zoneId && occupantType == z.occupantType && seat == z.seat);
        }

        public int hashCode() {
            LogTrace.enter("CarOccupantZoneManager", "hashCode");
            return LogTrace.result("CarOccupantZoneManager", "hashCode", java.util.Objects.hash(zoneId, occupantType, seat));
        }

        public String toString() {
            LogTrace.enter("CarOccupantZoneManager", "toString");
            return LogTrace.result("CarOccupantZoneManager", "toString", "OccupantZoneInfo{" + zoneId + "," + occupantType + "," + seat + "}");
        }
    }
}
