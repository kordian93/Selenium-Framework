package base;

import lombok.extern.log4j.Log4j2;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

@Log4j2
public abstract class BaseMethodTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void baseBeforeMethod(Method method) {
        initContext(coreConfig, bsConfig);
    }

    @AfterMethod(alwaysRun = true)
    public void baseAfterMethod(ITestResult result) {
        super.baseAfterMethod(result);
        clearContext();
    }
}
