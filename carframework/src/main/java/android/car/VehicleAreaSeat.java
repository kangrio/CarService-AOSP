package android.car;

public final class VehicleAreaSeat {
    public static final int SEAT_ROW_1_CENTER = 2;
    public static final int SEAT_ROW_1_LEFT = 1;
    public static final int SEAT_ROW_1_RIGHT = 4;
    public static final int SEAT_ROW_2_CENTER = 32;
    public static final int SEAT_ROW_2_LEFT = 16;
    public static final int SEAT_ROW_2_RIGHT = 64;
    public static final int SEAT_ROW_3_CENTER = 512;
    public static final int SEAT_ROW_3_LEFT = 256;
    public static final int SEAT_ROW_3_RIGHT = 1024;
    public static final int SEAT_UNKNOWN = 0;

    private VehicleAreaSeat() {
        LogTrace.enter("VehicleAreaSeat", "VehicleAreaSeat");
        LogTrace.voidResult("VehicleAreaSeat", "VehicleAreaSeat");
    }
}