package store.vxdesign.htat.tests.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

class TestExceptionExtension implements ExecutionCondition, TestExecutionExceptionHandler {
    private static final String skipTestKey = "SKIP_TEST";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static boolean skipTests(ExtensionContext context) {
        ExtensionContext containerContext = context.getParent().orElseThrow(IllegalStateException::new);
        return ExtensionContextUtils.getValueFromContext(containerContext, skipTestKey, ignoredKey -> false);
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
