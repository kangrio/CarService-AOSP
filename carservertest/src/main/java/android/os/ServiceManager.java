package android.os;

import android.util.ArrayMap;

import java.util.Map;

public class ServiceManager {
    private static Map<String, IBinder> sCache = new ArrayMap<String, IBinder>();
}
