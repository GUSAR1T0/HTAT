package store.vxdesign.htat.core.utilities.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShellCommand {
    public static final String defaultSeparator = " ";

    private final List<Argument> arguments = new ArrayList<>();

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

        public ShellCommand build() {
            return ShellCommand.this;
        }
    }

    private static String getSudo() {
        return String.join(defaultSeparator, "sudo", "-S", "-p", "''");
    }

    @Override
    public String toString() {
        return ((sudoer ? getSudo() : "") + defaultSeparator + command + defaultSeparator +
                arguments.stream().map(Argument::toString).collect(Collectors.joining(defaultSeparator))).trim();
    }
}
