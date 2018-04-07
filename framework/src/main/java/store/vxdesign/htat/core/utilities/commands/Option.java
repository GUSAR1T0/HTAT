package store.vxdesign.htat.core.utilities.commands;

public class Option implements Argument {
    private final String option;

    public Option(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return option;
    }
}
