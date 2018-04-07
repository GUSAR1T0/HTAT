package store.vxdesign.htat.core.exceptions;

public class InitializationException extends AbstractException {
    public InitializationException(String template, Object... objects) {
        super(template, objects);
    }
}
