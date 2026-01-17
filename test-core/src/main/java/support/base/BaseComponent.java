package support.base;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import support.context.ContextHolder;
import support.cselements.CSFieldDecorator;
import support.cselements.CSWebElement;
import support.utils.BrowserControl;
import support.utils.Wait;

@Log4j2
public abstract class BaseComponent {

    protected WebDriver driver;
    protected Wait wait;
    protected JavascriptExecutor js;
    protected Actions actions;
    protected BrowserControl browserControl;

    @FindBy(css = "div.loader")
    protected CSWebElement loader;

    public BaseComponent() {
        this.driver = ContextHolder.get().getDriver();
        this.js = ContextHolder.get().getJs();
        this.actions = ContextHolder.get().getActions();
        this.wait = ContextHolder.get().getWait();
        this.browserControl = ContextHolder.get().getBrowser();

        PageFactory.initElements(new CSFieldDecorator(this), this);
    }

    public void waitForLoaders() {
        wait.forPageToLoad();
        wait.forElementNotVisible(loader.getBy());
    }

    public void waitForPageToLoad() {
        wait.forPageToLoad();
    }

    protected void goToUrl(String url) {
        ContextHolder.get().getLastReproSteps().addStep("Go to URL: " + url);
        try {
            driver.get(url);
        } catch (TimeoutException e) {
            log.warn("Page load timeout exceeded, attempting to navigate using navigate().to()");
            driver.navigate().to(url);
        }
        wait.forPageToLoad();
    }

    // Moves the mouse to the bottom right corner to avoid hover effects
    public void moveCursorAway() {
        Long width = (Long) js.executeScript("return window.innerWidth;");
        Long height = (Long) js.executeScript("return window.innerHeight;");
        actions.moveToLocation((int) (width - 1), (int) (height - 1)).perform();
    }
}

