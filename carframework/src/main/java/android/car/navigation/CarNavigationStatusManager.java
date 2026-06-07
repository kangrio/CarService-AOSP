package android.car.navigation;

import android.car.Car;
import android.car.LogTrace;
import android.os.Bundle;
import android.os.IBinder;

public class CarNavigationStatusManager {
    public CarNavigationStatusManager() {
        LogTrace.enter("CarNavigationStatusManager", "CarNavigationStatusManager");

        LogTrace.voidResult("CarNavigationStatusManager", "CarNavigationStatusManager");
    }

    /**
     * Only for CarServiceLoader
     *
     * @hide
     */
    public CarNavigationStatusManager(Car car, IBinder service) {
        LogTrace.enter("CarNavigationStatusManager", "CarNavigationStatusManager", "car", car, "service", service);

        LogTrace.voidResult("CarNavigationStatusManager", "CarNavigationStatusManager");
    }

    public void sendEvent(int eventType, Bundle bundle) {
        LogTrace.enter("CarNavigationStatusManager", "sendEvent", "eventType", eventType, "bundle", bundle);
        sendNavigationStateChange(bundle);

        LogTrace.voidResult("CarNavigationStatusManager", "sendEvent");
    }

    public void sendNavigationStateChange(Bundle bundle) {
        LogTrace.enter("CarNavigationStatusManager", "sendNavigationStateChange", "bundle", bundle);


        LogTrace.voidResult("CarNavigationStatusManager", "sendNavigationStateChange");
    }

    public void onCarDisconnected() {
        LogTrace.enter("CarNavigationStatusManager", "onCarDisconnected");

        LogTrace.voidResult("CarNavigationStatusManager", "onCarDisconnected");
    }

    public CarNavigationInstrumentCluster getInstrumentClusterInfo() {
        LogTrace.enter("CarNavigationStatusManager", "getInstrumentClusterInfo");
        return LogTrace.result("CarNavigationStatusManager", "getInstrumentClusterInfo", new CarNavigationInstrumentCluster());
    }
}
