package store.vxdesign.htat.runners;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(value = "store.vxdesign.htat",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ConsoleRunner.class))
public class TestRunner {
}
