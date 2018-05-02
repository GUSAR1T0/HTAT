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
package store.vxdesign.htat.core.connections;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandResult {
    @Getter
    private LocalDateTime start;

    @Getter
    private LocalDateTime end;

    @Getter
    private String command;

    @Getter
    private Integer timeout;

    @Getter
    private Pattern pattern;

    @Getter
    private Integer status;

    @Getter
    private String output;

    @Getter
    private String error;

    public static CommandResultBuilder builder() {
        return new CommandResult().new CommandResultBuilder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class CommandResultBuilder {
        private CommandResultBuilder setter(Consumer<CommandResult> runnable) {
            runnable.accept(CommandResult.this);
            return this;
        }

        public CommandResultBuilder setStart(LocalDateTime start) {
            return setter(commandResult -> commandResult.start = start);
        }

        public CommandResultBuilder setEnd(LocalDateTime end) {
            return setter(commandResult -> commandResult.end = end);
        }

        public CommandResultBuilder setCommand(String command) {
            return setter(commandResult -> commandResult.command = command);
        }

        public CommandResultBuilder setTimeout(Integer timeout) {
            return setter(commandResult -> commandResult.timeout = timeout);
        }

        public CommandResultBuilder setPattern(Pattern pattern) {
            return setter(commandResult -> commandResult.pattern = pattern);
        }

        public CommandResultBuilder setStatus(Integer status) {
            return setter(commandResult -> commandResult.status = status);
        }

        public CommandResultBuilder setOutput(String output) {
            return setter(commandResult -> commandResult.output = output.trim());
        }

        public CommandResultBuilder setError(String error) {
            return setter(commandResult -> commandResult.error = error.trim());
        }

        public CommandResult build() {
            return CommandResult.this;
        }
    }

    @Override
    public String toString() {
        String timeout = this.timeout == null ? "unset" : this.timeout.toString();
        String pattern = this.pattern == null ? "unset" : this.pattern.toString();
        return String.format(
                String.format("%9s", "START") + ": %s%n" +
                        String.format("%9s", "END") + ": %s%n" +
                        String.format("%9s", "COMMAND") + ": %s%n" +
                        String.format("%9s", "TIMEOUT") + ": %s%n" +
                        String.format("%9s", "PATTERN") + ": %s%n" +
                        String.format("%9s", "STATUS") + ": %d%n" +
                        String.format("%9s", "OUTPUT") + ": %s%n" +
                        String.format("%9s", "ERROR") + ": %s",
                start, end, command, timeout, pattern, status, transform(output), transform(error));
    }

    private static String transform(String input) {
        String pad = String.join("", Collections.nCopies(11, " "));
        String[] lines = input.split("\\R");
        if (lines.length > 1) {
            return System.lineSeparator() + Stream.of(lines).map(pad::concat).collect(Collectors.joining(System.lineSeparator()));
        } else {
            return lines[0];
        }
    }
}

