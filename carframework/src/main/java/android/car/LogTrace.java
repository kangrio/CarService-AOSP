package android.car;

import android.util.Log;

import java.util.Arrays;

public final class LogTrace {
    private static final boolean DEBUG = false;

    private LogTrace() {
    }

    public static void enter(String tag, String method, Object... keyValues) {
        if (DEBUG)
            Log.w(tag, "call: " + method + " params: " + formatPairs(keyValues));
    }

    public static void voidResult(String tag, String method) {
        if (DEBUG)
            Log.w(tag, method + " result: void");
    }

    public static <T> T result(String tag, String method, T value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + formatValue(value));
        return value;
    }

    public static boolean result(String tag, String method, boolean value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static int result(String tag, String method, int value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static long result(String tag, String method, long value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static float result(String tag, String method, float value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static double result(String tag, String method, double value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static short result(String tag, String method, short value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static byte result(String tag, String method, byte value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    public static char result(String tag, String method, char value) {
        if (DEBUG)
            Log.w(tag, method + " result: " + value);
        return value;
    }

    private static String formatPairs(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyValues.length; i += 2) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            Object key = keyValues[i];
            Object value = i + 1 < keyValues.length ? keyValues[i + 1] : "<missing>";
            sb.append(key).append("=").append(formatValue(value));
        }
        return sb.toString();
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        Class<?> clazz = value.getClass();
        if (!clazz.isArray()) {
            return String.valueOf(value);
        }
        if (value instanceof Object[]) {
            return Arrays.deepToString((Object[]) value);
        }
        if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        }
        if (value instanceof long[]) {
            return Arrays.toString((long[]) value);
        }
        if (value instanceof float[]) {
            return Arrays.toString((float[]) value);
        }
        if (value instanceof double[]) {
            return Arrays.toString((double[]) value);
        }
        if (value instanceof boolean[]) {
            return Arrays.toString((boolean[]) value);
        }
        if (value instanceof byte[]) {
            return Arrays.toString((byte[]) value);
        }
        if (value instanceof short[]) {
            return Arrays.toString((short[]) value);
        }
        if (value instanceof char[]) {
            return Arrays.toString((char[]) value);
        }
        return String.valueOf(value);
    }
}
