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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;

@Parameters(separators = "=")
@Getter
public final class Arguments {
    @Parameter(names = {"--test-platform", "-p"}, description = "Test platform", required = true, converter = Converters.TestPlatformConverter.class)
    private ArgumentTypes.TestPlatform testPlatform = null;

    @Parameter(names = {"--test-unit-type", "-ut"}, description = "Test unit type for launching", required = true, converter = Converters.TestUnitTypeConverter.class)
    private ArgumentTypes.TestUnitType testUnitType = null;

    @Parameter(names = {"--test-instance-type", "-it"}, description = "Test instance type for launching", required = true, converter = Converters.TestInstanceTypeConverter.class)
    private ArgumentTypes.TestInstanceType testInstanceType = null;

    @Parameter(names = {"--test-name", "-t"}, description = "Test name that should be performed", required = true, validateValueWith = Validators.NonNullAndNonEmptyStringValidator.class)
    private String test = null;

    @Parameter(names = {"--properties", "-pf"}, description = "Path to properties file", validateValueWith = Validators.NonNullAndNonEmptyStringValidator.class)
    private String properties = null;

    @Parameter(names = {"--log-path", "-lp"}, description = "Path to log directory", validateValueWith = Validators.NonNullAndNonEmptyStringValidator.class)
    private String logPath = null;

    @Parameter(names = {"--log-level-spring", "-lls"}, description = "Logging level of Spring specific code", converter = Converters.LogLevelConverter.class)
    private ArgumentTypes.LogLevel springLogLevel = null;

    @Parameter(names = {"--log-level-application", "-lla"}, description = "Logging level of HTAT application code", converter = Converters.LogLevelConverter.class)
    private ArgumentTypes.LogLevel applicationLogLevel = null;

    @Parameter(names = {"--help", "-h"}, description = "Show help menu how to work with application", help = true)
    private boolean help = false;

    public void prepareVirtualMachineArguments() {
        if (properties != null) {
            System.setProperty("properties", properties);
        }
        if (logPath != null) {
            System.setProperty("log.path", logPath);
        }
        if (springLogLevel != null) {
            System.setProperty("log.level.spring", springLogLevel.toString());
        }
        if (applicationLogLevel != null) {
            System.setProperty("log.level.application", applicationLogLevel.toString());
        }
    }
}
