package android.car;

public final class VehicleAreaWheel {
    public static final int WHEEL_LEFT_FRONT = 1;
    public static final int WHEEL_LEFT_REAR = 4;
    public static final int WHEEL_RIGHT_FRONT = 2;
    public static final int WHEEL_RIGHT_REAR = 8;
    public static final int WHEEL_UNKNOWN = 0;

    private VehicleAreaWheel() {
        LogTrace.enter("VehicleAreaWheel", "VehicleAreaWheel");
        LogTrace.voidResult("VehicleAreaWheel", "VehicleAreaWheel");
    }
}