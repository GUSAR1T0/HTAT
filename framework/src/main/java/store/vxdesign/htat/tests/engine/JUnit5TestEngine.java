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
package store.vxdesign.htat.tests.engine;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import store.vxdesign.htat.core.utilities.cli.Arguments;
import store.vxdesign.htat.core.utilities.writers.SummaryWriter;
import store.vxdesign.htat.tests.common.TestStatus;
import store.vxdesign.htat.tests.common.TestTreeNode;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.EngineFilter.excludeEngines;

public final class JUnit5TestEngine {
    private final static String baseTestPackage = "store.vxdesign.htat.tests.platforms";

    private final Launcher launcher = LauncherFactory.create();
    private final SummaryGeneratingListener listener = new SummaryGeneratingListener();
    private final LauncherDiscoveryRequest request;
    private final TestPlan testPlan;

    public JUnit5TestEngine(Arguments arguments) {
        this.request = createLauncherDiscoveryRequest(arguments);
        this.launcher.registerTestExecutionListeners(listener);
        this.testPlan = launcher.discover(request);
    }

    private LauncherDiscoveryRequest createLauncherDiscoveryRequest(Arguments arguments) {
        String template = "%s.%s.%s.%s";
        String testInstance = String.format(template, baseTestPackage, arguments.getTestPlatform(), arguments.getTestUnitType(), arguments.getTest());

        DiscoverySelector selector = null;

        switch (arguments.getTestInstanceType()) {
            case METHOD:
                selector = selectMethod(testInstance);
                break;
            case CLASS:
                selector = selectClass(testInstance);
                break;
            case PACKAGE:
                selector = selectPackage(testInstance);
                break;
        }

        return LauncherDiscoveryRequestBuilder.request()
                .selectors(selector)
                .filters(
                        includeClassNamePatterns(".*"),
                        excludeEngines("junit-vintage")
                )
                .build();
    }

    public void launch() {
        TestResultsHandler.setConsole(true);

        launcher.execute(request);

        TestTreeNode tree = new TestTreeNode(null);
        createTree(testPlan, null, tree);

        SummaryWriter.create().write(tree, listener.getSummary());
    }

    private void createTree(TestPlan testPlan, TestIdentifier testIdentifier, TestTreeNode tree) {
        for (TestIdentifier test : (testIdentifier != null ? testPlan.getChildren(testIdentifier) : testPlan.getRoots())) {
            TestTreeNode newNode = new TestTreeNode(test);
            tree.addChild(newNode);

            if (test.isTest()) {
                if (listener.getSummary().getFailures().stream().anyMatch(failure -> failure.getTestIdentifier().equals(test))) {
                    newNode.setStatus(TestStatus.FAIL);
                } else if (TestResultsHandler.getSkippedTests().stream().anyMatch(skippedTest -> skippedTest.equals(test.getUniqueId()))) {
                    newNode.setStatus(TestStatus.SKIP);
                } else {
                    newNode.setStatus(TestStatus.SUCCESS);
                }
            }

            if (!testPlan.getChildren(test).isEmpty()) {
                createTree(testPlan, test, newNode);
            }
        }
    }

    public int getExitStatus() {
        return listener.getSummary().getTotalFailureCount() > 0 ? 1 : 0;
    }
}
