/*
 * Copyright (c) 2018 Roman Mashenkin <xromash@vxdesign.store>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
