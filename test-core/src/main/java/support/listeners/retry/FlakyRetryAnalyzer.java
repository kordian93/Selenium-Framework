package support.listeners.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import support.context.ContextHolder;

public class FlakyRetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (shouldRetry(result)) {
            retryCount++;
            result.setWasRetried(true);
            return true;
        }

        return false;
    }

    private boolean shouldRetry(ITestResult result) {
        if (!ContextHolder.get().getCoreConfig().isRepeats()) {
            return false;
        }

        for (String g : result.getMethod().getGroups()) {
            if ("bug".equalsIgnoreCase(g) || "blocked".equalsIgnoreCase(g)) {
                return false;
            }
        }

        return retryCount < MAX_RETRY_COUNT;
    }
}
