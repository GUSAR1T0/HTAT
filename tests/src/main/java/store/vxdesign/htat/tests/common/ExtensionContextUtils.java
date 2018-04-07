package store.vxdesign.htat.tests.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ExtensionContextUtils {
    static <T> void saveValueInContext(ExtensionContext context, String key, T value) {
        context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(key, value);
    }

    @SuppressWarnings("unchecked")
    static <T> T getValueFromContext(ExtensionContext context, String key, Function<String, T> function) {
        return (T) context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent(key, function);
    }
}
