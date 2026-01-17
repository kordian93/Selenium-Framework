package base;

import com.google.common.collect.ImmutableMap;

import config.PropertiesBootstrapConfig;
import config.TestConfig;
import config.TestDataConfig;
import io.qameta.allure.Allure;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

import pages.base.IPages;
import support.config.BrowserStackConfig;
import support.config.CoreConfig;
import support.context.ContextHolder;
import support.context.SpringContext;

import java.util.Objects;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

import support.listeners.BaseListener;

@SpringBootTest
@ContextConfiguration(classes = {PropertiesBootstrapConfig.class})
@Listeners({BaseListener.class})
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class
})
public abstract class BaseTest extends AbstractTestNGSpringContextTests implements IPages {

    @Autowired
    protected CoreConfig coreConfig;

    @Autowired
    protected BrowserStackConfig bsConfig;

    @Autowired
    protected TestConfig testConfig;

    @Autowired
    protected TestDataConfig testDataConfig;

    @AfterSuite(alwaysRun = true)
    public void baseAfterSuite() {
        if (coreConfig == null) {
            return;
        }

        Environment env = SpringContext.getApplicationContext().getBean(Environment.class);

        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Environment", coreConfig.getProfile().toUpperCase())
                        .put("Browser", coreConfig.getBrowser())
                        .put("Suite", env.getProperty("suiteXmlFile") == null ? "N/A" : Objects.requireNonNull(env.getProperty("suiteXmlFile")))
                        .build()
        );
    }

    @AfterMethod(alwaysRun = true)
    public void baseAfterMethod(ITestResult result) {
        if (!result.isSuccess()) {
            byte[] screenshot = takeScreenshot();
            String reproSteps = getReproSteps();
            String attempt = String.format("[attempt: %d]", result.getMethod().getCurrentInvocationCount());

            Allure.getLifecycle().addAttachment(
                    "Failure screenshot " + attempt, "image/png", "png", screenshot);
            Allure.getLifecycle().addAttachment(
                    "Repro steps " + attempt, "text/plain", "txt", reproSteps.getBytes());
        }

        ContextHolder.get().createNewReproSteps();
    }

    protected WebDriver getDriver() {
        return ContextHolder.get().getDriver();
    }

    private byte[] takeScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    private String getReproSteps() {
        return ContextHolder.get().getLastReproSteps().getSteps();
    }
}
