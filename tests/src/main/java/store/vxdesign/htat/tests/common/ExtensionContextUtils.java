/*
 * Copyright (c) 2018 Roman Mashenkin <xromash@vxdesign.store>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
