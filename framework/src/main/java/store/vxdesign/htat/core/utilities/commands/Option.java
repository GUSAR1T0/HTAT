package store.vxdesign.htat.core.utilities.commands;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Option implements Argument {
    private final String option;

    @Override
    public String toString() {
        return option;
    }
}
