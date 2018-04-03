package store.vxdesign.htat.fw.exceptions;

public class ConnectionException extends AbstractException {
    public ConnectionException(String template, Object... objects) {
        super(template, objects);
    }
}
