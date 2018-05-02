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
package store.vxdesign.htat.runners;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import lombok.AccessLevel;
import lombok.Getter;
import store.vxdesign.htat.core.utilities.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

final class CommandLineArguments {
    public static class TestInstanceValidator implements IValueValidator<String> {
        @Override
        public void validate(String name, String value) throws ParameterException {
            if (StringUtils.isNullOrEmpty(value)) {
                throw new ParameterException("Test instance should be set correctly");
            }
        }
    }

    @Parameter(names = {"--test", "-t"}, description = "Test case or suite for launching", required = true, validateValueWith = TestInstanceValidator.class)
    private String selectTest = null;

    @Parameter(names = {"--help", "-h"}, description = "Show help menu how to work with application", help = true)
    @Getter(AccessLevel.PACKAGE)
    private boolean help = false;

    String[] getArguments() {
        List<String> arguments = new ArrayList<>();

        // TODO: Add possibility to define test instance (method, class, package)
        arguments.add("--select-class");
        arguments.add(selectTest);

        arguments.add("--include-classname");
        arguments.add(".*");

        arguments.add("--exclude-engine");
        arguments.add("junit-vintage");

        return arguments.toArray(new String[0]);
    }
}
