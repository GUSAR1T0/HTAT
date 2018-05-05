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
package store.vxdesign.htat.core.utilities.cli;

import com.beust.jcommander.IStringConverter;
import store.vxdesign.htat.core.exceptions.ArgumentConversionException;
import store.vxdesign.htat.core.utilities.common.StringUtils;

import java.util.function.Supplier;

interface Converters {
    static <T> T convert(String value, Supplier<T> supplier) {
        if (!StringUtils.isNullOrEmpty(value)) {
            return supplier.get();
        } else {
            throw new ArgumentConversionException("Failed to convert command line argument value '%s'", value);
        }
    }

    class TestUnitTypeConverter implements IStringConverter<ArgumentTypes.TestUnitType> {
        @Override
        public ArgumentTypes.TestUnitType convert(String value) {
            return Converters.convert(value, () -> ArgumentTypes.TestUnitType.getValue(value));
        }
    }

    class TestPlatformConverter implements IStringConverter<ArgumentTypes.TestPlatform> {
        @Override
        public ArgumentTypes.TestPlatform convert(String value) {
            return Converters.convert(value, () -> ArgumentTypes.TestPlatform.getValue(value));
        }
    }

    class TestInstanceTypeConverter implements IStringConverter<ArgumentTypes.TestInstanceType> {
        @Override
        public ArgumentTypes.TestInstanceType convert(String value) {
            return Converters.convert(value, () -> ArgumentTypes.TestInstanceType.getValue(value));
        }
    }
}
