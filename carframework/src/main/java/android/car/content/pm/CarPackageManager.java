package android.car.content.pm;

import android.app.PendingIntent;
import android.car.LogTrace;

public final class CarPackageManager {
    public CarPackageManager() {
        LogTrace.enter("CarPackageManager", "CarPackageManager");
        LogTrace.voidResult("CarPackageManager", "CarPackageManager");
    }

    public boolean isActivityDistractionOptimized(String packageName, String className) {
        LogTrace.enter("CarPackageManager", "isActivityDistractionOptimized", "packageName", packageName, "className", className);
        return LogTrace.result("CarPackageManager", "isActivityDistractionOptimized", packageName != null && className != null);
    }

    public boolean isPendingIntentDistractionOptimized(PendingIntent pendingIntent) {
        LogTrace.enter("CarPackageManager", "isPendingIntentDistractionOptimized", "pendingIntent", pendingIntent);
        return LogTrace.result("CarPackageManager", "isPendingIntentDistractionOptimized", pendingIntent != null);
    }

    public boolean isServiceDistractionOptimized(String packageName, String className) {
        LogTrace.enter("CarPackageManager", "isServiceDistractionOptimized", "packageName", packageName, "className", className);
        return LogTrace.result("CarPackageManager", "isServiceDistractionOptimized", packageName != null && className != null);
    }
}
