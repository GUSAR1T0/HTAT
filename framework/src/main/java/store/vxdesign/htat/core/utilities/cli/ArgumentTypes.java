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

import java.util.Arrays;
import java.util.function.Predicate;

interface ArgumentTypes {
    static <T> T getValue(T[] values, Predicate<T> predicate) {
        return Arrays.stream(values).filter(predicate).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    enum TestPlatform {
        FUNCTIONAL_TEST("ft"), SYSTEM_TEST("st");

        private final String platform;

        TestPlatform(String platform) {
            this.platform = platform;
        }

        static TestPlatform getValue(String name) {
            return ArgumentTypes.getValue(values(), value -> value.platform.equals(name));
        }

        @Override
        public String toString() {
            return platform;
        }
    }

    enum TestUnitType {
        CASE("testcases"), SUITE("testsuites");

        private final String unitType;

        TestUnitType(String unitType) {
            this.unitType = unitType;
        }

        static TestUnitType getValue(String name) {
            return ArgumentTypes.getValue(values(), value -> value.name().equalsIgnoreCase(name));
        }

        @Override
        public String toString() {
            return unitType;
        }
    }

    enum TestInstanceType {
        METHOD, CLASS, PACKAGE;

        static TestInstanceType getValue(String name) {
            return ArgumentTypes.getValue(values(), value -> value.toString().equalsIgnoreCase(name));
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
