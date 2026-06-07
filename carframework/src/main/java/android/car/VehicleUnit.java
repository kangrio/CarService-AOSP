package android.car;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
public final class VehicleUnit {
    public static final int SHOULD_NOT_USE = 0x000;
    public static final int RPM = 0x02;
    public static final int HERTZ = 0x03;
    public static final int PERCENTILE = 0x10;
    public static final int MILLIMETER = 0x20;
    public static final int METER = 0x21;
    public static final int KILOMETER = 0x23;
    public static final int MILE = 0x24;
    public static final int METER_PER_SEC = 0x01;
    public static final int CELSIUS = 0x30;
    public static final int FAHRENHEIT = 0x31;
    public static final int KELVIN = 0x32;
    public static final int MILLILITER = 0x40;
    public static final int LITER = 0x41;
    public static final int US_GALLON  = 0x42;
    public static final int IMPERIAL_GALLON = 0x43;
    public static final int NANO_SECS = 0x50;
    public static final int SECS = 0x53;
    public static final int YEAR = 0x59;
    public static final int MILLIAMPERE = 0x61;
    public static final int MILLIVOLT = 0x62;
    public static final int MILLIWATTS = 0x63;
    public static final int WATT_HOUR = 0x60;
    public static final int AMPERE_HOURS = 0x64;
    public static final int KILOWATT_HOUR = 0x65;
    public static final int KILOPASCAL = 0x70;
    public static final int PSI = 0x71;
    public static final int BAR = 0x72;
    public static final int DEGREES = 0x80;
    public static final int MILES_PER_HOUR = 0x90;
    public static final int KILOMETERS_PER_HOUR = 0x91;
    public @interface Enum {}
    private VehicleUnit() {}
}