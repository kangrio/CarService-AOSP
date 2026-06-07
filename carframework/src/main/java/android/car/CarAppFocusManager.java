package android.car;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CopyOnWriteArrayList;

public final class CarAppFocusManager {
    public static final int APP_FOCUS_REQUEST_FAILED = 0;
    public static final int APP_FOCUS_REQUEST_SUCCEEDED = 1;
    public static final int APP_FOCUS_TYPE_NAVIGATION = 1;
    @Deprecated
    public static final int APP_FOCUS_TYPE_VOICE_COMMAND = 2;
    private final CopyOnWriteArrayList<OnAppFocusChangedListener> listeners = new CopyOnWriteArrayList<>();
    private OnAppFocusOwnershipCallback owner;

    public CarAppFocusManager() {
        LogTrace.enter("CarAppFocusManager", "CarAppFocusManager");

        LogTrace.voidResult("CarAppFocusManager", "CarAppFocusManager");
    }

    public void addFocusListener(OnAppFocusChangedListener listener, int appType) {
        LogTrace.enter("CarAppFocusManager", "addFocusListener", "listener", listener, "appType", appType);
        if (listener != null) {
            listeners.addIfAbsent(listener);
            listener.onAppFocusChanged(appType, true);
        }

        LogTrace.voidResult("CarAppFocusManager", "addFocusListener");
    }

    public void removeFocusListener(OnAppFocusChangedListener listener, int appType) {
        LogTrace.enter("CarAppFocusManager", "removeFocusListener", "listener", listener, "appType", appType);
        listeners.remove(listener);

        LogTrace.voidResult("CarAppFocusManager", "removeFocusListener");
    }

    public void removeFocusListener(OnAppFocusChangedListener listener) {
        LogTrace.enter("CarAppFocusManager", "removeFocusListener", "listener", listener);
        listeners.remove(listener);

        LogTrace.voidResult("CarAppFocusManager", "removeFocusListener");
    }

    public boolean isOwningFocus(OnAppFocusOwnershipCallback callback, int appType) {
        LogTrace.enter("CarAppFocusManager", "isOwningFocus", "callback", callback, "appType", appType);
        return LogTrace.result("CarAppFocusManager", "isOwningFocus", callback != null && callback == owner);
    }

    public int requestAppFocus(int appType, OnAppFocusOwnershipCallback ownershipCallback) {
        LogTrace.enter("CarAppFocusManager", "requestAppFocus", "appType", appType, "ownershipCallback", ownershipCallback);
        owner = ownershipCallback;
        if (ownershipCallback != null) ownershipCallback.onAppFocusOwnershipGranted(appType);
        Handler handler = new Handler(Looper.getMainLooper());
        for (OnAppFocusChangedListener l : listeners)
            handler.post(() -> l.onAppFocusChanged(appType, true));
        return LogTrace.result("CarAppFocusManager", "requestAppFocus", APP_FOCUS_REQUEST_SUCCEEDED);
    }

    public void abandonAppFocus(OnAppFocusOwnershipCallback ownershipCallback, int appType) {
        LogTrace.enter("CarAppFocusManager", "abandonAppFocus", "ownershipCallback", ownershipCallback, "appType", appType);
        if (owner == ownershipCallback) {
            if (owner != null) owner.onAppFocusOwnershipLost(appType);
            owner = null;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        for (OnAppFocusChangedListener l : listeners)
            handler.post(() -> l.onAppFocusChanged(appType, false));

        LogTrace.voidResult("CarAppFocusManager", "abandonAppFocus");
    }

    public void abandonAppFocus(OnAppFocusOwnershipCallback ownershipCallback) {
        LogTrace.enter("CarAppFocusManager", "abandonAppFocus", "ownershipCallback", ownershipCallback);
        abandonAppFocus(ownershipCallback, APP_FOCUS_TYPE_NAVIGATION);

        LogTrace.voidResult("CarAppFocusManager", "abandonAppFocus");
    }

    public interface OnAppFocusChangedListener {
        void onAppFocusChanged(int i, boolean z);
    }

    public interface OnAppFocusOwnershipCallback {
        void onAppFocusOwnershipGranted(int i);

        void onAppFocusOwnershipLost(int i);
    }
}
