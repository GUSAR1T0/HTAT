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
package store.vxdesign.htat.core.utilities.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShellCommand {
    public static final String defaultSeparator = " ";

    private final List<Argument> arguments = new ArrayList<>();
    private final List<ExpectAndSend> expectAndSends = new ArrayList<>();

    @Getter
    private boolean sudoer;

    private String command;

    public static ShellCommandBuilder builder() {
        return new ShellCommand().new ShellCommandBuilder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class ShellCommandBuilder {
        private ShellCommandBuilder setter(Consumer<ShellCommand> runnable) {
            runnable.accept(ShellCommand.this);
            return this;
        }

        public ShellCommandBuilder sudo() {
            return setter(shellCommand -> shellCommand.sudoer = true);
        }

        public ShellCommandBuilder command(String command) {
            return setter(shellCommand -> shellCommand.command = command);
        }

        public ShellCommandBuilder command(ShellCommand command) {
            return setter(shellCommand -> shellCommand.command = command.toString());
        }

        public ShellCommandBuilder arguments(Argument... arguments) {
            return setter(shellCommand -> shellCommand.arguments.addAll(Arrays.asList(arguments)));
        }

        public ShellCommandBuilder arguments(String... arguments) {
            return setter(shellCommand -> shellCommand.arguments.addAll(Arrays.stream(arguments).map(Option::new).collect(Collectors.toList())));
        }

        public ShellCommandBuilder expectAndSend(String expected, String command) {
            return setter(shellCommand -> shellCommand.expectAndSends.add(new ExpectAndSend(Pattern.compile(expected), command)));
        }

        public ShellCommandBuilder expectAndSend(Pattern expected, String command) {
            return setter(shellCommand -> shellCommand.expectAndSends.add(new ExpectAndSend(expected, command)));
        }

        public ShellCommand build() {
            return ShellCommand.this;
        }
    }

    private static String getSudo() {
        return String.join(defaultSeparator, "sudo", "-S", "-p", "''");
    }

    public Collection<ExpectAndSend> getExpectAndSends() {
        return Collections.unmodifiableCollection(expectAndSends);
    }

    @Override
    public String toString() {
        return ((sudoer ? getSudo() : "") + defaultSeparator + command + defaultSeparator +
                arguments.stream().map(Argument::toString).collect(Collectors.joining(defaultSeparator))).trim();
    }
}
