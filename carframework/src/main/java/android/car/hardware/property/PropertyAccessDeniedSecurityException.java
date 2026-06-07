package android.car.hardware.property;

import android.car.LogTrace;

public class PropertyAccessDeniedSecurityException extends SecurityException {
    public PropertyAccessDeniedSecurityException() {
        super();
        LogTrace.enter("PropertyAccessDeniedSecurityException", "PropertyAccessDeniedSecurityException");
        LogTrace.voidResult("PropertyAccessDeniedSecurityException", "PropertyAccessDeniedSecurityException");
    }

    public PropertyAccessDeniedSecurityException(String s) {
        super(s);
        LogTrace.enter("PropertyAccessDeniedSecurityException", "PropertyAccessDeniedSecurityException", "s", s);
        LogTrace.voidResult("PropertyAccessDeniedSecurityException", "PropertyAccessDeniedSecurityException");
    }
}
