package android.car.hardware.property;

import android.car.LogTrace;

public class PropertyNotAvailableException extends IllegalStateException {
    public PropertyNotAvailableException() {
        super();
        LogTrace.enter("PropertyNotAvailableException", "PropertyNotAvailableException");
        LogTrace.voidResult("PropertyNotAvailableException", "PropertyNotAvailableException");
    }

    public PropertyNotAvailableException(String s) {
        super(s);
        LogTrace.enter("PropertyNotAvailableException", "PropertyNotAvailableException", "s", s);
        LogTrace.voidResult("PropertyNotAvailableException", "PropertyNotAvailableException");
    }
}
