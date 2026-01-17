package support.listeners;

import io.qameta.allure.testng.AllureTestNg;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.IClassListener;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;
import support.config.CoreConfig;
import support.context.ContextHolder;
import support.context.SpringContext;
import support.utils.reprostepslogger.ReproStepsLogger;
import support.utils.reprostepslogger.ReproStepsStage;

@Log4j2
public class BaseListener extends AllureTestNg implements IClassListener, IExecutionListener {

    @Override
    public void onTestStart(ITestResult result) {
        log.info("TEST STARTED: {}", result.getMethod().getMethodName());
        super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("TEST SUCCESS: {}", result.getMethod().getMethodName());
        reportToBrowserStack(true, "Test passed");
        super.onTestSuccess(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.info("TEST FAILURE: {}", result.getMethod().getMethodName());
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Test failed";
        reportToBrowserStack(false, reason);
        super.onTestFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.info("TEST SKIPPED: {}", result.getMethod().getMethodName());
        reportToBrowserStack(false, "Test skipped");
        super.onTestSkipped(result);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        setReproStepsStage(method);
        super.beforeInvocation(method, result);
    }

    private void reportToBrowserStack(boolean passed, String reason) {
        CoreConfig config = SpringContext.getBean(CoreConfig.class);
        if (!config.isUseBrowserstack()) {
            return;
        }

        String status = passed ? "passed" : "failed";
        String safeReason = reason == null ? "" : reason.replace("\"", "\\\"");

        String script = "browserstack_executor: {\"action\": \"setSessionStatus\","
                + "\"arguments\": {\"status\":\"" + status + "\","
                + "\"reason\":\"" + safeReason + "\"}}";

        ((JavascriptExecutor) ContextHolder.get().getDriver()).executeScript(script);
    }

    private void setReproStepsStage(IInvokedMethod method) {
        if (ContextHolder.get() == null) {
            return;
        }

        ReproStepsLogger reproStepsLog = ContextHolder.get().getLastReproSteps();

        if (method.isTestMethod()) {
            switch (reproStepsLog.getStage()) {
                case NOT_STARTED, SETUP ->
                    reproStepsLog.setStage(ReproStepsStage.TEST);
                default -> {
                }
            }
        } else {
            switch (reproStepsLog.getStage()) {
                case NOT_STARTED ->
                    reproStepsLog.setStage(ReproStepsStage.SETUP);
                case TEST ->
                    reproStepsLog.setStage(ReproStepsStage.TEARDOWN);
                default -> {
                }
            }
        }
    }
}
