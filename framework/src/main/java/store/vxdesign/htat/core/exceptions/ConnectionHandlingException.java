package store.vxdesign.htat.core.exceptions;

public class ConnectionHandlingException extends AbstractException {
    public ConnectionHandlingException(String template, Object... objects) {
        super(template, objects);
    }
}
