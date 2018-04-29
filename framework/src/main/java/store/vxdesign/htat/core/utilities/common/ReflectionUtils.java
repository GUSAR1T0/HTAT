package store.vxdesign.htat.core.utilities.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {
    public static String getClassPrefix(Class<?> clazz) {
        try {
            return (String) clazz.getDeclaredField("prefix").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return clazz.getSimpleName().split("(?=\\p{Upper})")[0].toLowerCase();
        }
    }
}
