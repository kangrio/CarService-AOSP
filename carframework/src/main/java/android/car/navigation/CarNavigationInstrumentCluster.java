package android.car.navigation;

import android.car.LogTrace;
import android.os.Parcel;
import android.os.Parcelable;

public final class CarNavigationInstrumentCluster implements Parcelable {

    public static final Creator<CarNavigationInstrumentCluster> CREATOR = new Creator<CarNavigationInstrumentCluster>() {
        @Override
        public CarNavigationInstrumentCluster createFromParcel(Parcel in) {
            LogTrace.enter("CarNavigationInstrumentCluster", "createFromParcel", "in", in);
            return LogTrace.result("CarNavigationInstrumentCluster", "createFromParcel", new CarNavigationInstrumentCluster(in));
        }

        @Override
        public CarNavigationInstrumentCluster[] newArray(int size) {
            LogTrace.enter("CarNavigationInstrumentCluster", "newArray", "size", size);
            return LogTrace.result("CarNavigationInstrumentCluster", "newArray", new CarNavigationInstrumentCluster[size]);
        }
    };

    public CarNavigationInstrumentCluster() {
        LogTrace.enter("CarNavigationInstrumentCluster", "CarNavigationInstrumentCluster");


        LogTrace.voidResult("CarNavigationInstrumentCluster", "CarNavigationInstrumentCluster");
    }

    CarNavigationInstrumentCluster(Parcel in) {
        LogTrace.enter("CarNavigationInstrumentCluster", "CarNavigationInstrumentCluster", "in", in);

        LogTrace.voidResult("CarNavigationInstrumentCluster", "CarNavigationInstrumentCluster");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        LogTrace.enter("CarNavigationInstrumentCluster", "writeToParcel", "dest", dest, "flags", flags);

        LogTrace.voidResult("CarNavigationInstrumentCluster", "writeToParcel");
    }

    @Override
    public int describeContents() {
        LogTrace.enter("CarNavigationInstrumentCluster", "describeContents");
        return LogTrace.result("CarNavigationInstrumentCluster", "describeContents", 0);
    }
}