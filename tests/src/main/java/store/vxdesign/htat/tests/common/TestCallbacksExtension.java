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
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

class TestCallbacksExtension implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void beforeAll(ExtensionContext context) {
        Optional<TestEnvironment> annotation = AnnotationSupport.findAnnotation(context.getElement(), TestEnvironment.class);
        annotation.ifPresent(testEnvironment -> logger.info("Begin of the {}: {}", testEnvironment.unitType(), context.getDisplayName()));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Optional<TestEnvironment> annotation = AnnotationSupport.findAnnotation(context.getElement(), TestEnvironment.class);
        annotation.ifPresent(testEnvironment -> logger.info("End of the {}: {}", testEnvironment.unitType(), context.getDisplayName()));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        int id = ExtensionContextUtils.getValueFromContext(context, context.getTestClass().get().getName(), ignoredKey -> 0);
        logger.info("Begin of the test step: [{}] {}", id + 1, context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        int id = ExtensionContextUtils.getValueFromContext(context, context.getTestClass().get().getName(), ignoredKey -> 0);
        logger.info("End of the test step: [{}] {}", id + 1, context.getDisplayName());
        ExtensionContextUtils.saveValueInContext(context, context.getTestClass().get().getName(), id + 1);
    }
}
