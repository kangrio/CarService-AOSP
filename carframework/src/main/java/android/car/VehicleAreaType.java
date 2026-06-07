package android.car;

public final class VehicleAreaType {
    public static final int VEHICLE_AREA_TYPE_DOOR = 4;
    public static final int VEHICLE_AREA_TYPE_GLOBAL = 0;
    public static final int VEHICLE_AREA_TYPE_MIRROR = 5;
    public static final int VEHICLE_AREA_TYPE_SEAT = 3;
    public static final int VEHICLE_AREA_TYPE_WHEEL = 6;
    public static final int VEHICLE_AREA_TYPE_WINDOW = 2;

    private VehicleAreaType() {
        LogTrace.enter("VehicleAreaType", "VehicleAreaType");
        LogTrace.voidResult("VehicleAreaType", "VehicleAreaType");
    }
}