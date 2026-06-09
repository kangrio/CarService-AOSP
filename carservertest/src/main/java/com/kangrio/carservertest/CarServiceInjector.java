package com.kangrio.carservertest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Executors;

public class CarServiceInjector {
    private static final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                Log.i("CarServiceInjecter", "onServiceConnected");
                addService("car_service", service);
            } catch (Throwable e) {
                Log.e("CarServiceInjecter", "inject failed", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public static void addService(String name, IBinder service) {
        try {
            Class<?> carServiceClass = Class.forName("android.os.ServiceManager");
            Field sCacheField = carServiceClass.getDeclaredField("sCache");
            sCacheField.setAccessible(true);

            Map<String, IBinder> cache = (Map<String, IBinder>) sCacheField.get(null);
            cache.put(name, service);
        } catch (Throwable e) {
            Log.e("CarServiceInjecter", "inject failed", e);
        }
    }

    @SuppressWarnings("unused")
    public static void inject(Context context) {
        Intent intent = new Intent("android.car.ICar");
        intent.setPackage("com.kangrio.carserver");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.bindService(intent, Context.BIND_AUTO_CREATE, Executors.newSingleThreadExecutor(), serviceConnection);
        }
    }
}
