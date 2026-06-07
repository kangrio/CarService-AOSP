package android.car.hardware.property;

import android.car.LogTrace;
import android.car.VehicleAreaSeat;
import android.car.VehiclePropertyIds;
import android.car.VehicleUnit;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CarPropertyManager {

    public static final int CAR_SET_PROPERTY_ERROR_CODE_ACCESS_DENIED = 4;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_INVALID_ARG = 2;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_PROPERTY_NOT_AVAILABLE = 3;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_TRY_AGAIN = 1;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_UNKNOWN = 5;
    public static final float SENSOR_RATE_FAST = 10f;
    public static final float SENSOR_RATE_FASTEST = 100f;
    public static final float SENSOR_RATE_NORMAL = 1f;
    public static final float SENSOR_RATE_ONCHANGE = 0f;
    public static final float SENSOR_RATE_UI = 5f;
    private static final ConcurrentHashMap<Integer, Object> props = new ConcurrentHashMap<>();

    static {
        props.put(VehiclePropertyIds.INFO_MAKE, "BYD");
        props.put(VehiclePropertyIds.INFO_MODEL, "ATTO3");
        props.put(VehiclePropertyIds.INFO_MODEL_YEAR, 2023);

        props.put(VehiclePropertyIds.INFO_DRIVER_SEAT, VehicleAreaSeat.SEAT_ROW_1_LEFT);
        props.put(VehiclePropertyIds.DISTANCE_DISPLAY_UNITS, VehicleUnit.KILOMETERS_PER_HOUR); // Km/h
//        props.put(VehiclePropertyIds.EV_CHARGE_PORT_CONNECTED, false);
//
//        props.put(VehiclePropertyIds.PARKING_BRAKE_ON, false);
//        props.put(VehiclePropertyIds.GEAR_SELECTION, VehicleGear.GEAR_DRIVE);
//        props.put(VehiclePropertyIds.IGNITION_STATE, CarSensorEvent.IGNITION_STATE_ON);
    }

    private final CopyOnWriteArrayList<Holder> callbacks = new CopyOnWriteArrayList<>();

    public CarPropertyManager() {
        LogTrace.enter("CarPropertyManager", "CarPropertyManager");
        LogTrace.voidResult("CarPropertyManager", "CarPropertyManager");
    }

    public boolean registerCallback(final CarPropertyEventCallback callback, final int propertyId, final float updateRateHz) {
        LogTrace.enter("CarPropertyManager", "registerCallback", "callback", callback, "propertyId", propertyId, "updateRateHz", updateRateHz);
        boolean isPropertyAvailable = isPropertyAvailable(propertyId, 0);
        if (callback == null){
            return LogTrace.result("CarPropertyManager", "registerCallback", false);
        }
        if (!isPropertyAvailable) {
            return LogTrace.result("CarPropertyManager", "registerCallback", false);
        }
        // prevent duplicate callback for same property
        callbacks.removeIf(holder ->
                holder.c == callback && holder.p == propertyId
        );
        final Holder h = new Holder(callback, propertyId);
        callbacks.add(h);
        new Thread(() -> {
            while (callbacks.contains(h)) {
                try {
                    callback.onChangeEvent(getProperty(propertyId, 0));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(updateRateHz <= 0 ? 1000 : Math.max(50, (long) (1000f / updateRateHz)));
                } catch (Throwable ignored) {
                }
                /* comment below for need real time update */
                callbacks.removeIf(holder -> holder.c == callback);
            }
        }, "FakeCarProperty-" + propertyId).start();
        return LogTrace.result("CarPropertyManager", "registerCallback", true);
    }

    public void unregisterCallback(CarPropertyEventCallback callback) {
        LogTrace.enter("CarPropertyManager", "unregisterCallback", "callback", callback);
        callbacks.removeIf(h -> h.c == callback);
        LogTrace.voidResult("CarPropertyManager", "unregisterCallback");
    }

    public void unregisterCallback(CarPropertyEventCallback callback, int propertyId) {
        LogTrace.enter("CarPropertyManager", "unregisterCallback", "callback", callback, "propertyId", propertyId);
        callbacks.removeIf(h -> h.c == callback && h.p == propertyId);
        LogTrace.voidResult("CarPropertyManager", "unregisterCallback");
    }

    public List<CarPropertyConfig> getPropertyList() {
        LogTrace.enter("CarPropertyManager", "getPropertyList");
        List<CarPropertyConfig> list = new ArrayList<>();
        for (Map.Entry<Integer, Object> p : props.entrySet()) {
            list.add(getCarPropertyConfig(p.getKey()));
        }
        return LogTrace.result("CarPropertyManager", "getPropertyList", list);
    }

    public List<CarPropertyConfig> getPropertyList(ArraySet<Integer> propertyIds) {
        LogTrace.enter("CarPropertyManager", "getPropertyList", "propertyIds", propertyIds);
        List<CarPropertyConfig> list = new ArrayList<>();
        for (Map.Entry<Integer, Object> p : props.entrySet()) {
            if (propertyIds.contains(p.getKey())) list.add(getCarPropertyConfig(p.getKey()));
        }
        return LogTrace.result("CarPropertyManager", "getPropertyList", list);
    }

    public CarPropertyConfig<?> getCarPropertyConfig(int propId) {
        LogTrace.enter("CarPropertyManager", "getCarPropertyConfig", "propId", propId);
        return LogTrace.result("CarPropertyManager", "getCarPropertyConfig", new CarPropertyConfig(propId, Object.class));
    }

    public int getAreaId(int propId, int area) {
        LogTrace.enter("CarPropertyManager", "getAreaId", "propId", propId, "area", area);
        return LogTrace.result("CarPropertyManager", "getAreaId", area);
    }

    public boolean isPropertyAvailable(int propId, int area) {
        LogTrace.enter("CarPropertyManager", "isPropertyAvailable", "propId", propId, "area", area);
        return LogTrace.result("CarPropertyManager", "isPropertyAvailable", props.containsKey(propId));
    }

    public boolean getBooleanProperty(int prop, int area) {
        LogTrace.enter("CarPropertyManager", "getBooleanProperty", "prop", prop, "area", area);
        Object v = valueFor(prop);
        return LogTrace.result("CarPropertyManager", "getBooleanProperty", v instanceof Boolean ? (Boolean) v : false);
    }

    public float getFloatProperty(int prop, int area) {
        LogTrace.enter("CarPropertyManager", "getFloatProperty", "prop", prop, "area", area);
        Object v = valueFor(prop);
        return LogTrace.result("CarPropertyManager", "getFloatProperty", v instanceof Number ? ((Number) v).floatValue() : 0f);
    }

    public int getIntProperty(int prop, int area) {
        LogTrace.enter("CarPropertyManager", "getIntProperty", "prop", prop, "area", area);
        Object v = valueFor(prop);
        return LogTrace.result("CarPropertyManager", "getIntProperty", v instanceof Number ? ((Number) v).intValue() : 0);
    }

    public int[] getIntArrayProperty(int prop, int area) {
        LogTrace.enter("CarPropertyManager", "getIntArrayProperty", "prop", prop, "area", area);

        Object v = valueFor(prop);
        return LogTrace.result("CarPropertyManager", "getIntArrayProperty", v instanceof int[] ? (int[]) v : new int[0]);
    }

    public <E> CarPropertyValue<E> getProperty(Class<E> clazz, int propId, int areaId) {
        LogTrace.enter("CarPropertyManager", "getProperty", "clazz", clazz, "propId", propId, "areaId", areaId);
        CarPropertyValue<E> pValue = new CarPropertyValue<E>(propId, areaId, (E) valueFor(propId));
        if (pValue.getValue() != null) {
            return LogTrace.result("CarPropertyManager", "getProperty", pValue);
        }

        if (clazz == Boolean.class) {
            Object v = true;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Float.class) {
            Object v = 1.0;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Integer.class) {
            Object v = 1;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == int[].class) {
            Object v = new int[0];
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Long.class) {
            Object v = 1L;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Double.class) {
            Object v = 1.0;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Short.class) {
            Object v = 1;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == Byte.class) {
            Object v = 1;
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else if (clazz == String.class) {
            Object v = "66";
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        } else {
            Object v = new Object();
            return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) v));
        }

    }

    public <E> CarPropertyValue<E> getProperty(int propId, int areaId) {
        LogTrace.enter("CarPropertyManager", "getProperty", "propId", propId, "areaId", areaId);
        return LogTrace.result("CarPropertyManager", "getProperty", new CarPropertyValue<E>(propId, areaId, (E) valueFor(propId)));
    }

    public <E> void setProperty(Class<E> clazz, int propId, int areaId, E val) {
        LogTrace.enter("CarPropertyManager", "setProperty", "clazz", clazz, "propId", propId, "areaId", areaId, "val", val);
        props.put(propId, val);
        fire(propId, areaId, val);
        LogTrace.voidResult("CarPropertyManager", "setProperty");
    }

    public void setBooleanProperty(int prop, int areaId, boolean val) {
        LogTrace.enter("CarPropertyManager", "setBooleanProperty", "prop", prop, "areaId", areaId, "val", val);
        setProperty(Boolean.class, prop, areaId, val);
        LogTrace.voidResult("CarPropertyManager", "setBooleanProperty");
    }

    public void setFloatProperty(int prop, int areaId, float val) {
        LogTrace.enter("CarPropertyManager", "setFloatProperty", "prop", prop, "areaId", areaId, "val", val);
        setProperty(Float.class, prop, areaId, val);
        LogTrace.voidResult("CarPropertyManager", "setFloatProperty");
    }

    public void setIntProperty(int prop, int areaId, int val) {
        LogTrace.enter("CarPropertyManager", "setIntProperty", "prop", prop, "areaId", areaId, "val", val);
        setProperty(Integer.class, prop, areaId, val);
        LogTrace.voidResult("CarPropertyManager", "setIntProperty");
    }

    private Object valueFor(int prop) {
        LogTrace.enter("CarPropertyManager", "valueFor", "prop", prop);
        return LogTrace.result("CarPropertyManager", "valueFor", props.getOrDefault(prop, null));
    }

    private void fire(int prop, int area, Object val) {
        LogTrace.enter("CarPropertyManager", "fire", "prop", prop, "area", area, "val", val);
        CarPropertyValue v = new CarPropertyValue(prop, area, val);
        for (Holder h : callbacks) if (h.p == prop) h.c.onChangeEvent(v);
        LogTrace.voidResult("CarPropertyManager", "fire");
    }

    public interface CarPropertyEventCallback {
        void onChangeEvent(CarPropertyValue carPropertyValue);

        void onErrorEvent(int i, int i2);

        default void onErrorEvent(int propId, int areaId, int errorCode) {
            LogTrace.enter("CarPropertyManager", "onErrorEvent", "propId", propId, "areaId", areaId, "errorCode", errorCode);
            onErrorEvent(propId, areaId);
            LogTrace.voidResult("CarPropertyManager", "onErrorEvent");
        }
    }

    private static class Holder {
        CarPropertyEventCallback c;
        int p;

        Holder(CarPropertyEventCallback c, int p) {
            LogTrace.enter("CarPropertyManager", "Holder", "c", c, "p", p);
            this.c = c;
            this.p = p;
        }
    }
}
