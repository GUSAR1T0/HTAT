package store.vxdesign.htat.core.connections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionPatterns {
    public static final Pattern login = Pattern.compile("login:");

    public static final Pattern password = Pattern.compile("Password:");

    public static final Pattern prompt = Pattern.compile("(\\S*)@(\\S*):(\\S*)\\$$");
}
