package store.vxdesign.htat.core.exceptions;

public class ConnectionTimeoutException extends AbstractException {
    public ConnectionTimeoutException(String template, Object... objects) {
        super(template, objects);
    }
}
