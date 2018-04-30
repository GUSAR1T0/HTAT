package store.vxdesign.htat.tests.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.vxdesign.htat.runner.TestRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest(classes = TestRunner.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(TestCallbacksExtension.class)
@Nested
public @interface TestEnvironment {
    TestUnitType unitType();

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    enum TestUnitType {
        SINGLE_CASE("test case"), CASE_OF_SUITE("test case of suite"), SUITE("suite");

        private final String unitTypeName;

        @Override
        public String toString() {
            return unitTypeName;
        }
    }
}
