package android.car.hardware.power;

import android.car.LogTrace;
import android.util.Log;

import java.util.Arrays;

public final class CarPowerPolicyFilter {

    private final int[] components;

    public CarPowerPolicyFilter(int[] components) {
        LogTrace.enter("CarPowerPolicyFilter", "CarPowerPolicyFilter", "components", components);
        Log.w("CarPowerPolicyFilter", "CarPowerPolicyFilter: components=" + Arrays.toString(components));
        this.components = components == null ? new int[0] : components.clone();

        LogTrace.voidResult("CarPowerPolicyFilter", "CarPowerPolicyFilter");
    }

    public int[] getComponents() {
        LogTrace.enter("CarPowerPolicyFilter", "getComponents");
        Log.w("CarPowerPolicyFilter", "getComponents");
        return LogTrace.result("CarPowerPolicyFilter", "getComponents", components.clone());
    }
}