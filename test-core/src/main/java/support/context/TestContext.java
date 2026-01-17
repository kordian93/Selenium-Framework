package support.context;

import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import support.config.CoreConfig;
import support.utils.BrowserControl;
import support.utils.Wait;
import support.utils.reprostepslogger.ReproStepsLogger;

import java.time.Duration;
import java.util.ArrayList;

@Getter
public class TestContext {

    private final WebDriver driver;
    private final CoreConfig coreConfig;
    private final Wait wait;
    private final BrowserControl browser;
    private final Actions actions;
    private final JavascriptExecutor js;

    private final ArrayList<ReproStepsLogger> reproSteps = new ArrayList<>();

    public TestContext(WebDriver driver, CoreConfig coreConfig) {
        this.driver = driver;
        this.coreConfig = coreConfig;

        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        this.wait = new Wait(driver, js, actions, coreConfig.getDefaultTimeout(), coreConfig.getDefaultImplicitWait());
        this.browser = new BrowserControl(driver);

        this.reproSteps.add(new ReproStepsLogger());
    }

    public void createNewReproSteps() {
        reproSteps.add(new ReproStepsLogger());
    }

    public ReproStepsLogger getLastReproSteps() {
        return reproSteps.getLast();
    }

    public Wait getWait() {
        wait.changeImplicitWait(Duration.ofSeconds(coreConfig.getDefaultImplicitWait()));
        return wait;
    }
}
