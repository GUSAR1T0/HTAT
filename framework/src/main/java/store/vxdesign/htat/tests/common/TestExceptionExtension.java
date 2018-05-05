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
package store.vxdesign.htat.tests.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import store.vxdesign.htat.tests.engine.TestResultsHandler;

import java.util.Optional;

class TestExceptionExtension implements ExecutionCondition, TestExecutionExceptionHandler {
    private static final String skipTestKey = "SKIP_TEST";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static boolean skipTests(ExtensionContext context) {
        ExtensionContext containerContext = context.getParent().orElseThrow(IllegalStateException::new);
        boolean skipTests = ExtensionContextUtils.getValueFromContext(containerContext, skipTestKey, ignoredKey -> false);
        if (TestResultsHandler.isConsole() && skipTests) {
            TestResultsHandler.getSkippedTests().add(context.getUniqueId());
        }
        return skipTests;
    }

    private ConditionEvaluationResult disableIfSkipTestIsRequired(ExtensionContext context) {
        return skipTests(context) ? ConditionEvaluationResult.disabled("") : ConditionEvaluationResult.enabled("");
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (skipTests(context)) {
            return ConditionEvaluationResult.disabled("");
        }

        Optional<TestExecution> annotation = AnnotationSupport.findAnnotation(context.getElement(), TestExecution.class);
        if (annotation.isPresent() && TestExecution.ExecutionType.CRITICAL.equals(annotation.get().executionType())) {
            return disableIfSkipTestIsRequired(context);
        }
        return ConditionEvaluationResult.enabled("");
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Optional<TestExecution> annotation = AnnotationSupport.findAnnotation(context.getElement(), TestExecution.class);
        if (annotation.isPresent() && TestExecution.ExecutionType.CRITICAL.equals(annotation.get().executionType())) {
            ExtensionContextUtils.saveValueInContext(context, skipTestKey, true);
            logger.fatal("Exception was thrown when critical test step was executing", throwable);
            logger.info("All following tests will be ignored");
        } else {
            logger.error("Exception was thrown when test step was executing", throwable);
        }
        throw throwable;
    }
}
