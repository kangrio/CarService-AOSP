package android.car.media;

import android.car.LogTrace;

import java.util.concurrent.CopyOnWriteArrayList;

public final class CarAudioManager {
    private final CopyOnWriteArrayList<CarVolumeCallback> callbacks = new CopyOnWriteArrayList<>();


    public CarAudioManager() {
        LogTrace.enter("CarAudioManager", "CarAudioManager");

        LogTrace.voidResult("CarAudioManager", "CarAudioManager");
    }

    public void registerCarVolumeCallback(CarVolumeCallback callback) {
        LogTrace.enter("CarAudioManager", "registerCarVolumeCallback", "callback", callback);
        if (callback != null) callbacks.addIfAbsent(callback);

        LogTrace.voidResult("CarAudioManager", "registerCarVolumeCallback");
    }

    public void unregisterCarVolumeCallback(CarVolumeCallback callback) {
        LogTrace.enter("CarAudioManager", "unregisterCarVolumeCallback", "callback", callback);
        callbacks.remove(callback);

        LogTrace.voidResult("CarAudioManager", "unregisterCarVolumeCallback");
    }

    public abstract class CarVolumeCallback {
        public CarVolumeCallback() {
            LogTrace.enter("CarAudioManager", "CarVolumeCallback");
        }

        public void onGroupVolumeChanged(int zoneId, int groupId, int flags) {
            LogTrace.enter("CarAudioManager", "onGroupVolumeChanged", "zoneId", zoneId, "groupId", groupId, "flags", flags);

            LogTrace.voidResult("CarAudioManager", "onGroupVolumeChanged");
        }

        public void onMasterMuteChanged(int zoneId, int flags) {
            LogTrace.enter("CarAudioManager", "onMasterMuteChanged", "zoneId", zoneId, "flags", flags);

            LogTrace.voidResult("CarAudioManager", "onMasterMuteChanged");
        }
    }
}
