package android.car;

public final class VehicleGear {
    public static final int GEAR_DRIVE = 8;
    public static final int GEAR_EIGHTH = 2048;
    public static final int GEAR_FIFTH = 256;
    public static final int GEAR_FIRST = 16;
    public static final int GEAR_FOURTH = 128;
    public static final int GEAR_NEUTRAL = 1;
    public static final int GEAR_NINTH = 4096;
    public static final int GEAR_PARK = 4;
    public static final int GEAR_REVERSE = 2;
    public static final int GEAR_SECOND = 32;
    public static final int GEAR_SEVENTH = 1024;
    public static final int GEAR_SIXTH = 512;
    public static final int GEAR_THIRD = 64;
    public static final int GEAR_UNKNOWN = 0;

    private VehicleGear() {
        LogTrace.enter("VehicleGear", "VehicleGear");

        LogTrace.voidResult("VehicleGear", "VehicleGear");
    }

    public static String toString(int o) {
        LogTrace.enter("VehicleGear", "toString", "o", o);
        switch (o) {
            case GEAR_DRIVE:
                return LogTrace.result("VehicleGear", "toString", "DRIVE");
            case GEAR_REVERSE:
                return LogTrace.result("VehicleGear", "toString", "REVERSE");
            case GEAR_PARK:
                return LogTrace.result("VehicleGear", "toString", "PARK");
            case GEAR_NEUTRAL:
                return LogTrace.result("VehicleGear", "toString", "NEUTRAL");
            case GEAR_FIRST:
                return LogTrace.result("VehicleGear", "toString", "FIRST");
            case GEAR_SECOND:
                return LogTrace.result("VehicleGear", "toString", "SECOND");
            case GEAR_THIRD:
                return LogTrace.result("VehicleGear", "toString", "THIRD");
            case GEAR_FOURTH:
                return LogTrace.result("VehicleGear", "toString", "FOURTH");
            case GEAR_FIFTH:
                return LogTrace.result("VehicleGear", "toString", "FIFTH");
            case GEAR_SIXTH:
                return LogTrace.result("VehicleGear", "toString", "SIXTH");
            case GEAR_SEVENTH:
                return LogTrace.result("VehicleGear", "toString", "SEVENTH");
            case GEAR_EIGHTH:
                return LogTrace.result("VehicleGear", "toString", "EIGHTH");
            case GEAR_NINTH:
                return LogTrace.result("VehicleGear", "toString", "NINTH");
            default:
                return LogTrace.result("VehicleGear", "toString", "UNKNOWN(" + o + ")");
        }
    }
}
