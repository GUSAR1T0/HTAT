package store.vxdesign.htat.tests.common;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TestExceptionExtension.class)
public @interface TestExecution {
    ExecutionType executionType();

    enum ExecutionType {
        SIMPLE, CRITICAL
    }
}
