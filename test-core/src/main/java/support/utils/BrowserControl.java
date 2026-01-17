package support.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import support.base.BaseComponent;
import support.context.ContextHolder;

public class BrowserControl {

    private final WebDriver driver;
    private final ThreadLocal<String> previousWindow = new ThreadLocal<>();

    public BrowserControl(WebDriver driver) {
        this.driver = driver;
    }

    public void switchToNewWindow() {
        previousWindow.set(driver.getWindowHandle());

        ContextHolder.get().getWait().createFluentWait()
                .until(d -> d.getWindowHandles().size() > 1);

        String latestWindow = driver.getWindowHandles()
                .stream()
                .filter(handle -> !handle.equals(previousWindow.get()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("New window never appeared"));

        driver.switchTo().window(latestWindow);
    }

    public void switchToPreviousWindow() {
        String previous = previousWindow.get();

        ContextHolder.get().getWait().createFluentWait()
                .until(d -> d.getWindowHandles().contains(previous));

        String targetWindow = driver.getWindowHandles()
                .stream()
                .filter(handle -> handle.equals(previous))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Previous window no longer exists"));

        driver.switchTo().window(targetWindow);
        ContextHolder.get().getWait().sleepFor(250);
    }

    public void switchToFrame(String frameId) {
        driver.switchTo().frame(frameId);
    }

    public void switchToTopFrame() {
        driver.switchTo().defaultContent();
    }

    public void scrollToTop(BaseComponent page) {
        page.waitForLoaders();
        ContextHolder.get().getJs().executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom(BaseComponent page) {
        page.waitForLoaders();
        ContextHolder.get().getJs().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void clearBrowserData() {
        JavascriptExecutor js = ContextHolder.get().getJs();

        driver.manage().deleteAllCookies();
        js.executeScript("window.sessionStorage.clear();");
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.indexedDB.deleteDatabase('ngStorage');");
        driver.navigate().refresh();
    }
}
