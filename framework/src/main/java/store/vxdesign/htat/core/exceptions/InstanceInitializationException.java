package store.vxdesign.htat.core.exceptions;

public class InstanceInitializationException extends AbstractException {
    public InstanceInitializationException(String template, Object... objects) {
        super(template, objects);
    }
}
