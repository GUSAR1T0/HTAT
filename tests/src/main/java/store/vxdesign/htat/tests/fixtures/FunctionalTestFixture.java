package store.vxdesign.htat.tests.fixtures;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import store.vxdesign.htat.hosts.HostHolder;
import store.vxdesign.htat.tests.common.TestEnvironment;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface FunctionalTestFixture extends Fixture {
    @BeforeAll
    @Override
    default void setup(@Autowired HostHolder holder) {
        TestEnvironment testEnvironment = getClass().getAnnotation(TestEnvironment.class);
        if (testEnvironment != null && testEnvironment.unitType() != TestEnvironment.TestUnitType.CASE_OF_SUITE) {
            preparation("begin", holder::initialize);
        }
    }

    @AfterAll
    @Override
    default void teardown(@Autowired HostHolder holder) {
        TestEnvironment testEnvironment = getClass().getAnnotation(TestEnvironment.class);
        if (testEnvironment != null && testEnvironment.unitType() != TestEnvironment.TestUnitType.CASE_OF_SUITE) {
            preparation("end", holder::stop);
        }
    }
}
