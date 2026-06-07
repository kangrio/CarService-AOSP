package android.car.hardware.property;

import android.car.LogTrace;

public class CarInternalErrorException extends RuntimeException {
    public CarInternalErrorException() {
        super();
        LogTrace.enter("CarInternalErrorException", "CarInternalErrorException");
        LogTrace.voidResult("CarInternalErrorException", "CarInternalErrorException");
    }

    public CarInternalErrorException(String s) {
        super(s);
        LogTrace.enter("CarInternalErrorException", "CarInternalErrorException", "s", s);
        LogTrace.voidResult("CarInternalErrorException", "CarInternalErrorException");
    }
}
