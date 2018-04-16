package store.vxdesign.htat.core.utilities.commands;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Parameter implements Argument {
    private final String key;
    private final String separator;
    private final String value;

    public Parameter(String key, String value) {
        this(key, ShellCommand.defaultSeparator, value);
    }

    @Override
    public String toString() {
        return key + separator + value;
    }
}
