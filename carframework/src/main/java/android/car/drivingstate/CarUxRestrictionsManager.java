package android.car.drivingstate;

import android.car.LogTrace;

public final class CarUxRestrictionsManager {
    private OnUxRestrictionsChangedListener listener;

    public CarUxRestrictionsManager() {
        LogTrace.enter("CarUxRestrictionsManager", "CarUxRestrictionsManager");

        LogTrace.voidResult("CarUxRestrictionsManager", "CarUxRestrictionsManager");
    }

    public void registerListener(OnUxRestrictionsChangedListener listener) {
        LogTrace.enter("CarUxRestrictionsManager", "registerListener", "listener", listener);
        this.listener = listener;
        if (listener != null) listener.onUxRestrictionsChanged(getCurrentCarUxRestrictions());

        LogTrace.voidResult("CarUxRestrictionsManager", "registerListener");
    }

    public void unregisterListener() {
        LogTrace.enter("CarUxRestrictionsManager", "unregisterListener");
        listener = null;

        LogTrace.voidResult("CarUxRestrictionsManager", "unregisterListener");
    }

    public CarUxRestrictions getCurrentCarUxRestrictions() {
        LogTrace.enter("CarUxRestrictionsManager", "getCurrentCarUxRestrictions");
        return LogTrace.result("CarUxRestrictionsManager", "getCurrentCarUxRestrictions", new CarUxRestrictions(false, CarUxRestrictions.UX_RESTRICTIONS_BASELINE, 0, 0, 0));
    }

    public interface OnUxRestrictionsChangedListener {
        void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions);
    }
}
