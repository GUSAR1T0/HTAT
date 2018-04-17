package store.vxdesign.htat.core.connections;

import store.vxdesign.htat.core.utilities.commands.ShellCommand;

import java.util.regex.Pattern;

public interface Shell {
    CommandResult execute(ShellCommand command);

    CommandResult execute(int timeout, ShellCommand command);

    CommandResult execute(String pattern, ShellCommand command);

    CommandResult execute(Pattern pattern, ShellCommand command);

    CommandResult execute(int timeout, Pattern pattern, ShellCommand command);

    CommandResult execute(int timeout, String pattern, ShellCommand command);

    void setTimeoutInSeconds(int timeoutInSeconds);

    void close();
}
