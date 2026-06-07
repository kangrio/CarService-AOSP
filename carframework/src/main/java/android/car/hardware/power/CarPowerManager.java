package android.car.hardware.power;

import android.car.LogTrace;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class CarPowerManager {

    public static final int STATE_INVALID = 0;
    public static final int STATE_WAIT_FOR_VHAL = 1;
    public static final int STATE_SUSPEND_ENTER = 2;
    public static final int STATE_SUSPEND_EXIT = 3;
    public static final int STATE_SHUTDOWN_ENTER = 5;
    public static final int STATE_ON = 6;
    public static final int STATE_SHUTDOWN_PREPARE = 7;
    public static final int STATE_SHUTDOWN_CANCELLED = 8;
    public static final int STATE_HIBERNATION_ENTER = 9;
    public static final int STATE_HIBERNATION_EXIT = 10;
    public static final int STATE_PRE_SHUTDOWN_PREPARE = 11;
    public static final int STATE_POST_SUSPEND_ENTER = 12;
    public static final int STATE_POST_SHUTDOWN_ENTER = 13;
    public static final int STATE_POST_HIBERNATION_ENTER = 14;
    private final Map<CarPowerPolicyListener, PolicyHolder> policyListeners =
            new HashMap<>();
    private final Map<Integer, Boolean> displayStates =
            new HashMap<>();
    private int powerState = STATE_ON;
    private Executor stateExecutor;
    private CarPowerStateListener stateListener;
    private CarPowerStateListenerWithCompletion completionListener;
    private CarPowerPolicy currentPolicy =
            new CarPowerPolicy("default_all_on", new int[0], new int[0]);

    public CarPowerManager() {
        LogTrace.enter("CarPowerManager", "CarPowerManager");

        LogTrace.voidResult("CarPowerManager", "CarPowerManager");
    }

    public static boolean isCompletionAllowed(int state) {
        LogTrace.enter("CarPowerManager", "isCompletionAllowed", "state", state);
        Log.w("CarPowerManager", "isCompletionAllowed: state=" + state);
        switch (state) {
            case STATE_PRE_SHUTDOWN_PREPARE:
            case STATE_SHUTDOWN_PREPARE:
            case STATE_SHUTDOWN_ENTER:
            case STATE_SUSPEND_ENTER:
            case STATE_HIBERNATION_ENTER:
            case STATE_POST_SHUTDOWN_ENTER:
            case STATE_POST_SUSPEND_ENTER:
            case STATE_POST_HIBERNATION_ENTER:
                return LogTrace.result("CarPowerManager", "isCompletionAllowed", true);
            default:
                return LogTrace.result("CarPowerManager", "isCompletionAllowed", false);
        }
    }

    public void requestShutdownOnNextSuspend() {
        LogTrace.enter("CarPowerManager", "requestShutdownOnNextSuspend");
        Log.w("CarPowerManager", "requestShutdownOnNextSuspend");
        setFakePowerState(STATE_SHUTDOWN_PREPARE);

        LogTrace.voidResult("CarPowerManager", "requestShutdownOnNextSuspend");
    }

    public void scheduleNextWakeupTime(int seconds) {
        LogTrace.enter("CarPowerManager", "scheduleNextWakeupTime", "seconds", seconds);
        Log.w("CarPowerManager", "scheduleNextWakeupTime");

        LogTrace.voidResult("CarPowerManager", "scheduleNextWakeupTime");
    }

    public int getPowerState() {
        LogTrace.enter("CarPowerManager", "getPowerState");
        Log.w("CarPowerManager", "getPowerState");
        return LogTrace.result("CarPowerManager", "getPowerState", powerState);
    }

    public void setListener(Executor executor, CarPowerStateListener listener) {
        LogTrace.enter("CarPowerManager", "setListener", "executor", executor, "listener", listener);
        Log.w("CarPowerManager", "setListener: executor=" + executor + ", listener=" + listener);
        if (executor == null) throw new IllegalArgumentException("executor cannot be null");
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");

        synchronized (this) {
            if (stateListener != null || completionListener != null) {
                throw new IllegalStateException("Listener must be cleared first");
            }

            stateExecutor = executor;
            stateListener = listener;
        }

        executor.execute(() -> listener.onStateChanged(powerState));

        LogTrace.voidResult("CarPowerManager", "setListener");
    }

    public void setListener(CarPowerStateListener listener) {
        LogTrace.enter("CarPowerManager", "setListener", "listener", listener);
        Log.w("CarPowerManager", "setListener: listener=" + listener);
        setListener(Runnable::run, listener);

        LogTrace.voidResult("CarPowerManager", "setListener");
    }

    public void setListenerWithCompletion(
            Executor executor,
            CarPowerStateListenerWithCompletion listener
    ) {
        LogTrace.enter("CarPowerManager", "setListenerWithCompletion", "executor", executor, "listener", listener);
        Log.w("CarPowerManager", "setListenerWithCompletion: executor=" + executor + ", listener=" + listener);
        if (executor == null) throw new IllegalArgumentException("executor cannot be null");
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");

        synchronized (this) {
            if (stateListener != null || completionListener != null) {
                throw new IllegalStateException("Listener must be cleared first");
            }

            stateExecutor = executor;
            completionListener = listener;
        }

        notifyPowerState(powerState);
        LogTrace.voidResult("CarPowerManager", "setListenerWithCompletion");
    }

    public void clearListener() {
        LogTrace.enter("CarPowerManager", "clearListener");
        Log.w("CarPowerManager", "clearListener");
        synchronized (this) {
            stateExecutor = null;
            stateListener = null;
            completionListener = null;
        }

        LogTrace.voidResult("CarPowerManager", "clearListener");
    }

    public CarPowerPolicy getCurrentPowerPolicy() {
        LogTrace.enter("CarPowerManager", "getCurrentPowerPolicy");
        Log.w("CarPowerManager", "getCurrentPowerPolicy");
        return LogTrace.result("CarPowerManager", "getCurrentPowerPolicy", currentPolicy);
    }

    public void applyPowerPolicy(String policyId) {
        LogTrace.enter("CarPowerManager", "applyPowerPolicy", "policyId", policyId);
        Log.w("CarPowerManager", "applyPowerPolicy: policyId=" + policyId);
        if (policyId == null) throw new IllegalArgumentException("Null policyId");

        currentPolicy = new CarPowerPolicy(policyId, new int[0], new int[0]);
        notifyPolicyListeners(currentPolicy);

        LogTrace.voidResult("CarPowerManager", "applyPowerPolicy");
    }

    public void setPowerPolicyGroup(String policyGroupId) {
        LogTrace.enter("CarPowerManager", "setPowerPolicyGroup", "policyGroupId", policyGroupId);
        Log.w("CarPowerManager", "setPowerPolicyGroup: policyGroupId=" + policyGroupId);
        if (policyGroupId == null) throw new IllegalArgumentException("Null policyGroupId");

        LogTrace.voidResult("CarPowerManager", "setPowerPolicyGroup");
    }

    public void addPowerPolicyListener(
            Executor executor,
            CarPowerPolicyFilter filter,
            CarPowerPolicyListener listener
    ) {
        LogTrace.enter("CarPowerManager", "addPowerPolicyListener", "executor", executor, "filter", filter, "listener", listener);
        Log.w("CarPowerManager", "addPowerPolicyListener: executor=" + executor + ", filter=" + filter + ", listener=" + listener);
        if (executor == null) throw new IllegalArgumentException("Null executor");
        if (filter == null) throw new IllegalArgumentException("Null filter");
        if (listener == null) throw new IllegalArgumentException("Null listener");

        synchronized (policyListeners) {
            policyListeners.put(listener, new PolicyHolder(executor, filter));
        }

        executor.execute(() -> listener.onPolicyChanged(currentPolicy));
        LogTrace.voidResult("CarPowerManager", "addPowerPolicyListener");
    }

    public void removePowerPolicyListener(CarPowerPolicyListener listener) {
        LogTrace.enter("CarPowerManager", "removePowerPolicyListener", "listener", listener);
        Log.w("CarPowerManager", "removePowerPolicyListener: listener=" + listener);
        if (listener == null) throw new IllegalArgumentException("Null listener");

        synchronized (policyListeners) {
            policyListeners.remove(listener);
        }

        LogTrace.voidResult("CarPowerManager", "removePowerPolicyListener");
    }

    public void setDisplayPowerState(int displayId, boolean enable) {
        LogTrace.enter("CarPowerManager", "setDisplayPowerState", "displayId", displayId, "enable", enable);
        Log.w("CarPowerManager", "setDisplayPowerState: displayId=" + displayId + ", enable=" + enable);
        displayStates.put(displayId, enable);

        LogTrace.voidResult("CarPowerManager", "setDisplayPowerState");
    }

    public boolean getDisplayPowerState(int displayId) {
        LogTrace.enter("CarPowerManager", "getDisplayPowerState", "displayId", displayId);
        Log.w("CarPowerManager", "getDisplayPowerState: displayId=" + displayId);
        Boolean value = displayStates.get(displayId);
        return LogTrace.result("CarPowerManager", "getDisplayPowerState", value == null || value);
    }

    public void notifyUserActivity(int displayId) {
        LogTrace.enter("CarPowerManager", "notifyUserActivity", "displayId", displayId);
        Log.w("CarPowerManager", "notifyUserActivity: displayId=" + displayId);

        LogTrace.voidResult("CarPowerManager", "notifyUserActivity");
    }

    public void setFakePowerState(int state) {
        LogTrace.enter("CarPowerManager", "setFakePowerState", "state", state);
        Log.w("CarPowerManager", "setFakePowerState: state=" + state);
        powerState = state;
        notifyPowerState(state);

        LogTrace.voidResult("CarPowerManager", "setFakePowerState");
    }

    private void notifyPowerState(final int state) {
        LogTrace.enter("CarPowerManager", "notifyPowerState", "state", state);
        Log.w("CarPowerManager", "notifyPowerState: state=" + state);
        final Executor executor;
        final CarPowerStateListener listener;
        final CarPowerStateListenerWithCompletion listenerWithCompletion;

        synchronized (this) {
            executor = stateExecutor;
            listener = stateListener;
            listenerWithCompletion = completionListener;
        }

        if (executor == null) {
            LogTrace.voidResult("CarPowerManager", "notifyPowerState");
            return;
        }

        if (listener != null) {
            executor.execute(() -> listener.onStateChanged(state));
        } else if (listenerWithCompletion != null) {
            CompletablePowerStateChangeFuture future = null;

            if (isCompletionAllowed(state)) {
                future = new FakeFuture(System.currentTimeMillis() + 5000);
            }

            CompletablePowerStateChangeFuture finalFuture = future;
            executor.execute(() -> listenerWithCompletion.onStateChanged(state, finalFuture));
        }

        LogTrace.voidResult("CarPowerManager", "notifyPowerState");
    }

    private void notifyPolicyListeners(CarPowerPolicy policy) {
        LogTrace.enter("CarPowerManager", "notifyPolicyListeners", "policy", policy);
        Log.w("CarPowerManager", "notifyPolicyListeners: policy=" + policy);
        Map<CarPowerPolicyListener, PolicyHolder> copy;

        synchronized (policyListeners) {
            copy = new HashMap<>(policyListeners);
        }

        for (Map.Entry<CarPowerPolicyListener, PolicyHolder> entry : copy.entrySet()) {
            CarPowerPolicyListener listener = entry.getKey();
            PolicyHolder holder = entry.getValue();

            holder.executor.execute(() -> listener.onPolicyChanged(policy));
        }

        LogTrace.voidResult("CarPowerManager", "notifyPolicyListeners");
    }

    public interface CompletablePowerStateChangeFuture {
        void complete();

        long getExpirationTime();
    }

    public interface CarPowerStateListener {
        void onStateChanged(int state);
    }

    public interface CarPowerStateListenerWithCompletion {
        void onStateChanged(int state, CompletablePowerStateChangeFuture future);
    }

    public interface CarPowerPolicyListener {
        void onPolicyChanged(CarPowerPolicy policy);
    }

    private static class PolicyHolder {
        final Executor executor;
        final CarPowerPolicyFilter filter;

        PolicyHolder(Executor executor, CarPowerPolicyFilter filter) {
            LogTrace.enter("CarPowerManager", "PolicyHolder", "executor", executor, "filter", filter);
            this.executor = executor;
            this.filter = filter;
            LogTrace.voidResult("CarPowerManager", "PolicyHolder");
        }
    }

    private static class FakeFuture implements CompletablePowerStateChangeFuture {
        private final long expirationTime;
        private boolean completed;

        FakeFuture(long expirationTime) {
            LogTrace.enter("CarPowerManager", "FakeFuture", "expirationTime", expirationTime);
            this.expirationTime = expirationTime;
            LogTrace.voidResult("CarPowerManager", "FakeFuture");
        }

        @Override
        public synchronized void complete() {
            LogTrace.enter("CarPowerManager", "complete");
            completed = true;

            LogTrace.voidResult("CarPowerManager", "complete");
        }

        @Override
        public long getExpirationTime() {
            LogTrace.enter("CarPowerManager", "getExpirationTime");
            return LogTrace.result("CarPowerManager", "getExpirationTime", expirationTime);
        }

        public synchronized boolean isCompleted() {
            LogTrace.enter("CarPowerManager", "isCompleted");
            return LogTrace.result("CarPowerManager", "isCompleted", completed);
        }
    }
}
