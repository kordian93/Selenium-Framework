package base;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public abstract class BaseClassTest extends BaseTest {

    @BeforeClass(alwaysRun = true)
    public void baseBeforeClass() {
        initContext(coreConfig, bsConfig);
    }

    @AfterClass(alwaysRun = true)
    public void baseAfterClass() {
        clearContext();
    }

    @AfterMethod(alwaysRun = true)
    public void baseAfterMethod(ITestResult result) {
        super.baseAfterMethod(result);
    }
}
