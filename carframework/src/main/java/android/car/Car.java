package android.car;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.util.Log;

public final class Car {

    public static final String APP_FOCUS_SERVICE = "app_focus";
    public static final String AUDIO_SERVICE = "audio";
    public static final String CAR_CONFIGURATION_SERVICE = "configuration";
    public static final String CAR_EXTRA_BROWSE_SERVICE_FOR_SESSION = "android.media.session.BROWSE_SERVICE";
    public static final String CAR_EXTRA_MEDIA_COMPONENT = "android.car.intent.extra.MEDIA_COMPONENT";
    public static final String CAR_INTENT_ACTION_MEDIA_TEMPLATE = "android.car.intent.action.MEDIA_TEMPLATE";
    public static final String CAR_NAVIGATION_SERVICE = "car_navigation_service";
    public static final String CAR_OCCUPANT_ZONE_SERVICE = "car_occupant_zone_service";
    public static final String CAR_UX_RESTRICTION_SERVICE = "uxrestriction";
    public static final long CAR_WAIT_TIMEOUT_DO_NOT_WAIT = 0;
    public static final long CAR_WAIT_TIMEOUT_WAIT_FOREVER = -1;
    public static final int CONNECTION_TYPE_EMBEDDED = 5;
    public static final String INFO_SERVICE = "info";
    public static final String POWER_SERVICE = "power";
    public static final String META_DATA_DISTRACTION_OPTIMIZED = "distractionOptimized";
    public static final String META_DATA_REQUIRES_CAR_FEATURE = "requires-car-feature";
    public static final String PACKAGE_SERVICE = "package";
    public static final String PERMISSION_CAR_CONTROL_AUDIO_SETTINGS = "com.kangrio.carservice.permission.CAR_CONTROL_AUDIO_SETTINGS";
    public static final String PERMISSION_CAR_CONTROL_AUDIO_VOLUME = "com.kangrio.carservice.permission.CAR_CONTROL_AUDIO_VOLUME";
    public static final String PERMISSION_CAR_INFO = "com.kangrio.carservice.permission.CAR_INFO";
    public static final String PERMISSION_CAR_NAVIGATION_MANAGER = "com.kangrio.carservice.permission.CAR_NAVIGATION_MANAGER";
    public static final String PERMISSION_CONTROL_DISPLAY_UNITS = "com.kangrio.carservice.permission.CONTROL_CAR_DISPLAY_UNITS";
    public static final String PERMISSION_CONTROL_INTERIOR_LIGHTS = "com.kangrio.carservice.permission.CONTROL_CAR_INTERIOR_LIGHTS";
    public static final String PERMISSION_ENERGY = "com.kangrio.carservice.permission.CAR_ENERGY";
    public static final String PERMISSION_ENERGY_PORTS = "com.kangrio.carservice.permission.CAR_ENERGY_PORTS";
    public static final String PERMISSION_EXTERIOR_ENVIRONMENT = "com.kangrio.carservice.permission.CAR_EXTERIOR_ENVIRONMENT";
    public static final String PERMISSION_IDENTIFICATION = "com.kangrio.carservice.permission.CAR_IDENTIFICATION";
    public static final String PERMISSION_POWERTRAIN = "com.kangrio.carservice.permission.CAR_POWERTRAIN";
    public static final String PERMISSION_READ_DISPLAY_UNITS = "com.kangrio.carservice.permission.READ_CAR_DISPLAY_UNITS";
    public static final String PERMISSION_READ_INTERIOR_LIGHTS = "com.kangrio.carservice.permission.READ_CAR_INTERIOR_LIGHTS";
    public static final String PERMISSION_READ_STEERING_STATE = "com.kangrio.carservice.permission.READ_CAR_STEERING";
    public static final String PERMISSION_SPEED = "com.kangrio.carservice.permission.CAR_SPEED";
    public static final String PROPERTY_SERVICE = "property";
    @Deprecated
    public static final String SENSOR_SERVICE = "sensor";
    private boolean connected = true;

    public Car() {
        LogTrace.enter("Car", "Car");

        LogTrace.voidResult("Car", "Car");
    }

    @Deprecated
    public static Car createCar(Context context, ServiceConnection serviceConnectionListener, Handler handler) {
        LogTrace.enter("Car", "createCar", "context", context, "serviceConnectionListener", serviceConnectionListener, "handler", handler);
        return LogTrace.result("Car", "createCar", new Car());
    }

    @Deprecated
    public static Car createCar(Context context, ServiceConnection serviceConnectionListener) {
        LogTrace.enter("Car", "createCar", "context", context, "serviceConnectionListener", serviceConnectionListener);
        return LogTrace.result("Car", "createCar", new Car());
    }

    public static Car createCar(Context context) {
        LogTrace.enter("Car", "createCar", "context", context);
        return LogTrace.result("Car", "createCar", new Car());
    }

    public static Car createCar(Context context, Handler handler) {
        LogTrace.enter("Car", "createCar", "context", context, "handler", handler);
        return LogTrace.result("Car", "createCar", new Car());
    }

    public static Car createCar(Context context, Handler handler, long waitTimeoutMs, CarServiceLifecycleListener statusChangeListener) {
        LogTrace.enter("Car", "createCar", "context", context, "handler", handler, "waitTimeoutMs", waitTimeoutMs, "statusChangeListener", statusChangeListener);
        Car car = new Car();
        if (statusChangeListener != null) statusChangeListener.onLifecycleChanged(car, true);
        return LogTrace.result("Car", "createCar", car);
    }

    public static void printData(String Tag, String... strings) {
        LogTrace.enter("Car", "printData", "Tag", Tag, "strings", strings);
        StringBuilder sb = new StringBuilder();
        sb.append(Tag);
        sb.append(": ");
        for (Object object : strings) {
            sb.append(object.toString());
            sb.append(", ");
        }
        Log.d("CarFramework", sb.toString());
        sb.setLength(0);

        LogTrace.voidResult("Car", "printData");
    }

    public static void printData(String Tag, int... ints) {
        LogTrace.enter("Car", "printData", "Tag", Tag, "ints", ints);
        StringBuilder sb = new StringBuilder();
        sb.append(Tag);
        sb.append(": ");
        for (Object object : ints) {
            sb.append(object);
            sb.append(", ");
        }
        Log.d("CarFramework", sb.toString());
        sb.setLength(0);

        LogTrace.voidResult("Car", "printData");
    }

    public static void printData(String Tag, long... longs) {
        LogTrace.enter("Car", "printData", "Tag", Tag, "longs", longs);
        StringBuilder sb = new StringBuilder();
        sb.append(Tag);
        sb.append(": ");
        for (Object object : longs) {
            sb.append(object);
            sb.append(", ");
        }
        Log.d("CarFramework", sb.toString());
        sb.setLength(0);

        LogTrace.voidResult("Car", "printData");
    }

    public static void printData(String Tag, float... floats) {
        LogTrace.enter("Car", "printData", "Tag", Tag, "floats", floats);
        StringBuilder sb = new StringBuilder();
        sb.append(Tag);
        sb.append(": ");
        for (Object object : floats) {
            sb.append(object);
            sb.append(", ");
        }
        Log.d("CarFramework", sb.toString());
        sb.setLength(0);

        LogTrace.voidResult("Car", "printData");
    }

    public static void printData(String Tag, boolean... booleans) {
        LogTrace.enter("Car", "printData", "Tag", Tag, "booleans", booleans);
        StringBuilder sb = new StringBuilder();
        sb.append(Tag);
        sb.append(": ");
        for (Object object : booleans) {
            sb.append(object);
            sb.append(", ");
        }
        Log.d("CarFramework", sb.toString());
        sb.setLength(0);

        LogTrace.voidResult("Car", "printData");
    }

    public static void testPrintData(int a, int b, int c) {
        LogTrace.enter("Car", "testPrintData", "a", a, "b", b, "c", c);
        String testPrintData = "testPrintData";
        printData(testPrintData, a, b, c);

        LogTrace.voidResult("Car", "testPrintData");
    }

    @Deprecated
    public void connect() throws IllegalStateException {
        LogTrace.enter("Car", "connect");
        connected = true;
        LogTrace.voidResult("Car", "connect");
    }

    public void disconnect() {
        LogTrace.enter("Car", "disconnect");
        connected = false;

        LogTrace.voidResult("Car", "disconnect");
    }

    public boolean isConnected() {
        LogTrace.enter("Car", "isConnected");
        return LogTrace.result("Car", "isConnected", connected);
    }

    public boolean isConnecting() {
        LogTrace.enter("Car", "isConnecting");
        return LogTrace.result("Car", "isConnecting", false);
    }

    public Object getCarManager(String serviceName) {
        LogTrace.enter("Car", "getCarManager", "serviceName", serviceName);
        if (PROPERTY_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.hardware.property.CarPropertyManager());
        if (SENSOR_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.hardware.CarSensorManager());
        if (CAR_UX_RESTRICTION_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.drivingstate.CarUxRestrictionsManager());
        if (CAR_NAVIGATION_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.navigation.CarNavigationStatusManager());
        if (INFO_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new CarInfoManager());
        if (APP_FOCUS_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new CarAppFocusManager());
        if (PACKAGE_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.content.pm.CarPackageManager());
        if (AUDIO_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.media.CarAudioManager());
        if (CAR_CONFIGURATION_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.settings.CarConfigurationManager());
        if (CAR_OCCUPANT_ZONE_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new CarOccupantZoneManager());
        if (POWER_SERVICE.equals(serviceName))
            return LogTrace.result("Car", "getCarManager", new android.car.hardware.power.CarPowerManager());
        return LogTrace.result("Car", "getCarManager", null);
    }

    public int getCarConnectionType() {
        LogTrace.enter("Car", "getCarConnectionType");
        return LogTrace.result("Car", "getCarConnectionType", CONNECTION_TYPE_EMBEDDED);
    }

    public boolean isFeatureEnabled(String featureName) {
        LogTrace.enter("Car", "isFeatureEnabled", "featureName", featureName);
        return LogTrace.result("Car", "isFeatureEnabled", true);
    }

    public interface CarServiceLifecycleListener {
        void onLifecycleChanged(Car car, boolean z);
    }
}
