package store.vxdesign.htat.core.exceptions;

public class PatternNotFoundException extends AbstractException {
    public PatternNotFoundException(String template, Object... objects) {
        super(template, objects);
    }
}
