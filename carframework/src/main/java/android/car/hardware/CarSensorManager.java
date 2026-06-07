package android.car.hardware;

import android.car.LogTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Deprecated
public class CarSensorManager {
    public static final int SENSOR_RATE_NORMAL = 1, SENSOR_RATE_UI = 5, SENSOR_RATE_FAST = 10, SENSOR_RATE_FASTEST = 100;
    public static final int SENSOR_TYPE_CAR_SPEED = 2, SENSOR_TYPE_RPM = 3, SENSOR_TYPE_ODOMETER = 4, SENSOR_TYPE_FUEL_LEVEL = 5, SENSOR_TYPE_PARKING_BRAKE = 287310850, SENSOR_TYPE_GEAR = 289408000, SENSOR_TYPE_IGNITION_STATE = 289408009;
    private final CopyOnWriteArrayList<Holder> listeners = new CopyOnWriteArrayList<>();

    public CarSensorManager() {
        LogTrace.enter("CarSensorManager", "CarSensorManager");

        LogTrace.voidResult("CarSensorManager", "CarSensorManager");
    }

    @Deprecated
    public int[] getSupportedSensors() {
        LogTrace.enter("CarSensorManager", "getSupportedSensors");
        return LogTrace.result("CarSensorManager", "getSupportedSensors", new int[]{SENSOR_TYPE_CAR_SPEED, SENSOR_TYPE_RPM, SENSOR_TYPE_ODOMETER, SENSOR_TYPE_FUEL_LEVEL, SENSOR_TYPE_GEAR, SENSOR_TYPE_IGNITION_STATE});
    }

    @Deprecated
    public List<CarPropertyConfig> getPropertyList() {
        LogTrace.enter("CarSensorManager", "getPropertyList");
        return LogTrace.result("CarSensorManager", "getPropertyList", new ArrayList<>());
    }

    @Deprecated
    public boolean isSensorSupported(int sensorType) {
        LogTrace.enter("CarSensorManager", "isSensorSupported", "sensorType", sensorType);
        return LogTrace.result("CarSensorManager", "isSensorSupported", true);
    }

    @Deprecated
    public boolean registerListener(final OnSensorChangedListener listener, final int sensorType, final int rate) {
        LogTrace.enter("CarSensorManager", "registerListener", "listener", listener, "sensorType", sensorType, "rate", rate);
        if (listener == null) return LogTrace.result("CarSensorManager", "registerListener", false);
        final Holder h = new Holder(listener, sensorType);
        listeners.add(h);
        new Thread(() -> {
            while (listeners.contains(h)) {
                listener.onSensorChanged(getLatestSensorEvent(sensorType));
                try {
                    Thread.sleep(Math.max(50, 1000 / Math.max(1, rate)));
                } catch (Throwable ignored) {
                }
            }
        }).start();
        return LogTrace.result("CarSensorManager", "registerListener", true);
    }

    @Deprecated
    public void unregisterListener(OnSensorChangedListener listener) {
        LogTrace.enter("CarSensorManager", "unregisterListener", "listener", listener);
        listeners.removeIf(h -> h.l == listener);

        LogTrace.voidResult("CarSensorManager", "unregisterListener");
    }

    @Deprecated
    public void unregisterListener(OnSensorChangedListener listener, int sensorType) {
        LogTrace.enter("CarSensorManager", "unregisterListener", "listener", listener, "sensorType", sensorType);
        listeners.removeIf(h -> h.l == listener && h.t == sensorType);

        LogTrace.voidResult("CarSensorManager", "unregisterListener");
    }

    @Deprecated
    public CarSensorEvent getLatestSensorEvent(int type) {
        LogTrace.enter("CarSensorManager", "getLatestSensorEvent", "type", type);
        switch (type) {
            case SENSOR_TYPE_CAR_SPEED:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[]{60f}));
            case SENSOR_TYPE_RPM:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[]{1500f}));
            case SENSOR_TYPE_ODOMETER:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[]{12345f}));
            case SENSOR_TYPE_FUEL_LEVEL:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[]{80f}));
            case SENSOR_TYPE_GEAR:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[0], new int[]{android.car.VehicleGear.GEAR_DRIVE}, new long[0]));
            case SENSOR_TYPE_IGNITION_STATE:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[0], new int[]{CarSensorEvent.IGNITION_STATE_ON}, new long[0]));
            default:
                return LogTrace.result("CarSensorManager", "getLatestSensorEvent", new CarSensorEvent(type, new float[]{0f}));
        }
    }

    @Deprecated
    public interface OnSensorChangedListener {
        @Deprecated
        void onSensorChanged(CarSensorEvent carSensorEvent);
    }

    private static class Holder {
        OnSensorChangedListener l;
        int t;

        Holder(OnSensorChangedListener l, int t) {
            LogTrace.enter("CarSensorManager", "Holder", "l", l, "t", t);
            this.l = l;
            this.t = t;
        }
    }
}
