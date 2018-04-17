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
