package store.vxdesign.htat.fw.connections;

import java.util.regex.Pattern;

public final class ConnectionPatterns {
    private ConnectionPatterns() {
    }

    public static final Pattern login = Pattern.compile("login:");

    public static final Pattern password = Pattern.compile("Password:");

    public static final Pattern prompt = Pattern.compile("\\$");
}
