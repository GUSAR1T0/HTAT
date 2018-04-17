package store.vxdesign.htat.tests.common;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TestExceptionExtension.class)
public @interface TestExecutionType {
    ExecutionType executionType() default ExecutionType.SIMPLE;

    enum ExecutionType {
        SIMPLE, CRITICAL
    }
}
