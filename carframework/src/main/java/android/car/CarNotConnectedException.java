package android.car;

@Deprecated
public class CarNotConnectedException extends RuntimeException {
    @Deprecated
    public CarNotConnectedException() {
        super();
        LogTrace.enter("CarNotConnectedException", "CarNotConnectedException");
        LogTrace.voidResult("CarNotConnectedException", "CarNotConnectedException");
    }

    @Deprecated
    public CarNotConnectedException(String name) {
        super(name);
        LogTrace.enter("CarNotConnectedException", "CarNotConnectedException", "name", name);
        LogTrace.voidResult("CarNotConnectedException", "CarNotConnectedException");
    }

    @Deprecated
    public CarNotConnectedException(String name, Throwable cause) {
        super(name, cause);
        LogTrace.enter("CarNotConnectedException", "CarNotConnectedException", "name", name, "cause", cause);
        LogTrace.voidResult("CarNotConnectedException", "CarNotConnectedException");
    }

    @Deprecated
    public CarNotConnectedException(Exception cause) {
        super(cause);
        LogTrace.enter("CarNotConnectedException", "CarNotConnectedException", "cause", cause);
        LogTrace.voidResult("CarNotConnectedException", "CarNotConnectedException");
    }
}
