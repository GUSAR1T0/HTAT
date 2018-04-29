package store.vxdesign.htat.runners;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import lombok.AccessLevel;
import lombok.Getter;
import store.vxdesign.htat.core.utilities.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

final class CommandLineArguments {
    public static class TestInstanceValidator implements IValueValidator<String> {
        @Override
        public void validate(String name, String value) throws ParameterException {
            if (StringUtils.isNullOrEmpty(value)) {
                throw new ParameterException("Test instance should be set correctly");
            }
        }
    }

    @Parameter(names = {"--test", "-t"}, description = "Test case or suite for launching", required = true, validateValueWith = TestInstanceValidator.class)
    private String selectTest = null;

    @Parameter(names = {"--help", "-h"}, description = "Show help menu how to work with application", help = true)
    @Getter(AccessLevel.PACKAGE)
    private boolean help = false;

    String[] getArguments() {
        List<String> arguments = new ArrayList<>();

        // TODO: Add possibility to define test instance (method, class, package)
        arguments.add("--select-class");
        arguments.add(selectTest);

        arguments.add("--include-classname");
        arguments.add(".*");

        arguments.add("--exclude-engine");
        arguments.add("junit-vintage");

        return arguments.toArray(new String[0]);
    }
}
