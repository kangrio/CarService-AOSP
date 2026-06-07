package android.car.settings;

import android.car.LogTrace;

@Deprecated
public class CarConfigurationManager {
    public CarConfigurationManager() {
        LogTrace.enter("CarConfigurationManager", "CarConfigurationManager");
        LogTrace.voidResult("CarConfigurationManager", "CarConfigurationManager");
    }

    @Deprecated
    public SpeedBumpConfiguration getSpeedBumpConfiguration() {
        LogTrace.enter("CarConfigurationManager", "getSpeedBumpConfiguration");
        return LogTrace.result("CarConfigurationManager", "getSpeedBumpConfiguration", new SpeedBumpConfiguration(1.0, 5.0, 1000L));
    }
}
