package android.car.hardware.property;

import android.car.LogTrace;

public class PropertyNotAvailableAndRetryException extends IllegalStateException {
    public PropertyNotAvailableAndRetryException() {
        super();
        LogTrace.enter("PropertyNotAvailableAndRetryException", "PropertyNotAvailableAndRetryException");
        LogTrace.voidResult("PropertyNotAvailableAndRetryException", "PropertyNotAvailableAndRetryException");
    }

    public PropertyNotAvailableAndRetryException(String s) {
        super(s);
        LogTrace.enter("PropertyNotAvailableAndRetryException", "PropertyNotAvailableAndRetryException", "s", s);
        LogTrace.voidResult("PropertyNotAvailableAndRetryException", "PropertyNotAvailableAndRetryException");
    }
}
