package store.vxdesign.htat.fw.exceptions;

abstract class AbstractException extends RuntimeException {
    AbstractException(String template, Object... objects) {
        super(format(template, objects));
    }

    private static String format(String template, Object... objects) {
        return String.format(template, objects);
    }
}
