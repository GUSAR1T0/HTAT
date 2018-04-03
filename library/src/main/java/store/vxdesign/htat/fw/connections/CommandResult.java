package store.vxdesign.htat.fw.connections;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandResult {
    @Getter
    private LocalDateTime start;

    @Getter
    private LocalDateTime end;

    @Getter
    private String command;

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

    public class CommandResultBuilder {
        private CommandResultBuilder() {
        }

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
        return String.format(
                String.format("%9s", "START") + ": %s%n" +
                        String.format("%9s", "END") + ": %s%n" +
                        String.format("%9s", "COMMAND") + ": %s%n" +
                        String.format("%9s", "PATTERN") + ": %s%n" +
                        String.format("%9s", "STATUS") + ": %d%n" +
                        String.format("%9s", "OUTPUT") + ": %s%n" +
                        String.format("%9s", "ERROR") + ": %s",
                start, end, command, pattern, status, transform(output), transform(error));
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

