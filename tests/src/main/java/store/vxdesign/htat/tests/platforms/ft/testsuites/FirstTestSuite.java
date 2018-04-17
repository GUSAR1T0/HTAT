package store.vxdesign.htat.tests.platforms.ft.testsuites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import store.vxdesign.htat.tests.common.TestEnvironment;
import store.vxdesign.htat.tests.fixtures.FunctionalTestFixture;
import store.vxdesign.htat.tests.platforms.ft.testcases.FirstTestCase;

@DisplayName("The first functional test suite")
@TestEnvironment(platform = TestEnvironment.Platform.FUNCTIONAL_TEST, unitType = TestEnvironment.TestUnitType.SUITE)
public class FirstTestSuite implements FunctionalTestFixture {
    @Nested
    @DisplayName("The third test case of functional test suite")
    @TestEnvironment(platform = TestEnvironment.Platform.FUNCTIONAL_TEST, unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    public class TestCase03 extends FirstTestCase {
    }

    @Nested
    @DisplayName("The second test case of functional test suite")
    @TestEnvironment(platform = TestEnvironment.Platform.FUNCTIONAL_TEST, unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    public class TestCase02 extends FirstTestCase {
    }

    @Nested
    @DisplayName("The first test case of functional test suite")
    @TestEnvironment(platform = TestEnvironment.Platform.FUNCTIONAL_TEST, unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    public class TestCase01 extends FirstTestCase {
    }
}
