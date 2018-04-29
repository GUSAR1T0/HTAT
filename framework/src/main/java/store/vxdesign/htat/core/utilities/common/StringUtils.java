package store.vxdesign.htat.core.utilities.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
