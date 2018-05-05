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
package store.vxdesign.htat.tests.runners;

import com.beust.jcommander.JCommander;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import store.vxdesign.htat.core.utilities.cli.Arguments;
import store.vxdesign.htat.tests.engine.JUnit5TestEngine;

@SpringBootApplication(scanBasePackages = "store.vxdesign.htat")
public class ConsoleRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        Arguments parsedArguments = new Arguments();
        JCommander commandLineArguments = new JCommander(parsedArguments);
        commandLineArguments.parse(args);
        commandLineArguments.setProgramName("Host Test Automation Tool");
        if (parsedArguments.isHelp()) {
            commandLineArguments.usage();
        } else {
            JUnit5TestEngine engine = new JUnit5TestEngine(parsedArguments);
            engine.launch();
            System.exit(engine.getExitStatus());
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsoleRunner.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
