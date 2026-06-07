package android.car.hardware.power;

import android.car.LogTrace;
import android.util.Log;

import java.util.Arrays;

public final class CarPowerPolicy {

    private final String policyId;
    private final int[] enabledComponents;
    private final int[] disabledComponents;

    public CarPowerPolicy(String policyId, int[] enabledComponents, int[] disabledComponents) {
        LogTrace.enter("CarPowerPolicy", "CarPowerPolicy", "policyId", policyId, "enabledComponents", enabledComponents, "disabledComponents", disabledComponents);
        Log.w("CarPowerPolicy", "CarPowerPolicy: policyId=" + policyId + ", enabled=" + Arrays.toString(enabledComponents) + ", disabled=" + Arrays.toString(disabledComponents));
        this.policyId = policyId == null ? "" : policyId;
        this.enabledComponents = enabledComponents == null ? new int[0] : enabledComponents.clone();
        this.disabledComponents = disabledComponents == null ? new int[0] : disabledComponents.clone();

        LogTrace.voidResult("CarPowerPolicy", "CarPowerPolicy");
    }

    public String getPolicyId() {
        LogTrace.enter("CarPowerPolicy", "getPolicyId");
        Log.w("CarPowerPolicy", "getPolicyId");
        return LogTrace.result("CarPowerPolicy", "getPolicyId", policyId);
    }

    public int[] getEnabledComponents() {
        LogTrace.enter("CarPowerPolicy", "getEnabledComponents");
        Log.w("CarPowerPolicy", "getEnabledComponents");
        return LogTrace.result("CarPowerPolicy", "getEnabledComponents", enabledComponents.clone());
    }

    public int[] getDisabledComponents() {
        LogTrace.enter("CarPowerPolicy", "getDisabledComponents");
        Log.w("CarPowerPolicy", "getDisabledComponents");
        return LogTrace.result("CarPowerPolicy", "getDisabledComponents", disabledComponents.clone());
    }

    @Override
    public String toString() {
        LogTrace.enter("CarPowerPolicy", "toString");
        return LogTrace.result("CarPowerPolicy", "toString", "CarPowerPolicy{id=" + policyId
                + ", enabled=" + Arrays.toString(enabledComponents)
                + ", disabled=" + Arrays.toString(disabledComponents)
                + "}");
    }
}