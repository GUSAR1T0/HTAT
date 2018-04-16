package store.vxdesign.htat.core.utilities.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class ExpectAndSend {
    private final Pattern expected;
    private final String command;
}
