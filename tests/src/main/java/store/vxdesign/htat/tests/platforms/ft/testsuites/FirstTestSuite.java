package store.vxdesign.htat.tests.platforms.ft.testsuites;

import org.junit.jupiter.api.DisplayName;
import store.vxdesign.htat.tests.common.TestEnvironment;
import store.vxdesign.htat.tests.fixtures.FunctionalTestFixture;
import store.vxdesign.htat.tests.platforms.ft.testcases.FirstTestCase;

@DisplayName("The first functional test suite")
@TestEnvironment(unitType = TestEnvironment.TestUnitType.SUITE)
class FirstTestSuite implements FunctionalTestFixture {
    @DisplayName("The third test case of functional test suite")
    @TestEnvironment(unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    class TestCase03 extends FirstTestCase {
    }

    @DisplayName("The second test case of functional test suite")
    @TestEnvironment(unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    class TestCase02 extends FirstTestCase {
    }

    @DisplayName("The first test case of functional test suite")
    @TestEnvironment(unitType = TestEnvironment.TestUnitType.CASE_OF_SUITE)
    class TestCase01 extends FirstTestCase {
    }
}
