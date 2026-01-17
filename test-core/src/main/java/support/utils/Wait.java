package support.utils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import support.cselements.CSWebElement;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Log4j2
public class Wait {

    @Getter
    private final Duration DEFAULT_TIMEOUT;
    private final Duration POLLING_INTERVAL = Duration.ofMillis(250);
    private final Duration DEFAULT_IMPLICIT_WAIT;

    private final WebDriver driver;
    private final JavascriptExecutor js;
    private final Actions actions;

    public Wait(WebDriver driver, JavascriptExecutor js, Actions actions, int timeout, int implicitWait) {
        this.driver = driver;
        this.js = js;
        this.actions = actions;
        this.DEFAULT_TIMEOUT = Duration.ofSeconds(timeout);
        this.DEFAULT_IMPLICIT_WAIT = Duration.ofSeconds(implicitWait);
    }

    public FluentWait<WebDriver> createFluentWait() {
        return createFluentWait(DEFAULT_TIMEOUT, POLLING_INTERVAL);
    }

    public FluentWait<WebDriver> createFluentWait(Duration timeout, Duration polling) {
        changeImplicitWait(Duration.ofMillis(100));
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public void sleepFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while sleeping: " + e.getMessage());
        }
    }

    public boolean isPresent(CSWebElement el) {
        return isPresent(el, DEFAULT_TIMEOUT);
    }

    public boolean isPresent(CSWebElement el, Duration timeout) {
        try {
            return createFluentWait(timeout, POLLING_INTERVAL)
                    .until(f -> !driver.findElements(el.getBy()).isEmpty());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isNotPresent(CSWebElement el) {
        return isNotPresent(el.getBy());
    }

    public boolean isNotPresent(By by) {
        try {
            return createFluentWait().until(f -> driver.findElements(by).isEmpty());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void forPageToLoad() {
        sleepFor(250);
        createFluentWait().until((ExpectedCondition<Boolean>) f
                -> "complete".equals(js.executeScript("return document.readyState")));
    }

    public void forUrlContains(String url) {
        createFluentWait().until(ExpectedConditions.urlContains(url));
    }

    public WebElement forElementVisible(CSWebElement el) {
        return forElementVisible(el, DEFAULT_TIMEOUT);
    }

    public WebElement forElementVisible(CSWebElement el, Duration timeout) {
        try {
            return createFluentWait(timeout, POLLING_INTERVAL)
                    .until(ExpectedConditions.visibilityOfElementLocated(el.getBy()));
        } catch (TimeoutException e) {
            return null;
        }
    }

    public boolean forElementNotVisible(CSWebElement el) {
        return forElementNotVisible(el, DEFAULT_TIMEOUT);
    }

    public boolean forElementNotVisible(CSWebElement el, Duration duration) {
        if (isNotPresent(el)) {
            return true;
        }

        try {
            return createFluentWait(duration, POLLING_INTERVAL)
                    .until(ExpectedConditions.invisibilityOfElementLocated(el.getBy()));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean forElementNotVisible(By by) {
        try {
            return createFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void forElementClickable(CSWebElement el) {
        createFluentWait().until(ExpectedConditions.elementToBeClickable(el.getBy()));
    }

    public boolean forAttributeContains(WebElement element, String attributeName, String text) {
        return createFluentWait().until(ExpectedConditions.attributeContains(element, attributeName, text));
    }

    public boolean forAttributeContains(By by, String attribute, String value) {
        return forAttributeContains(by, attribute, value, DEFAULT_TIMEOUT);
    }

    public boolean forAttributeContains(By by, String attribute, String value, Duration timeout) {
        return createFluentWait(timeout, POLLING_INTERVAL).until(f -> {
            List<WebElement> elements = driver.findElements(by);
            return elements.stream().anyMatch(el -> {
                try {
                    return Objects.requireNonNull(el.getDomAttribute(attribute)).contains(value);
                } catch (TimeoutException | StaleElementReferenceException | NullPointerException e) {
                    return false;
                }
            });
        });
    }

    public void forTextMatches(CSWebElement el, Pattern pattern) {
        createFluentWait().until(ExpectedConditions.textMatches(el.getBy(), pattern));
    }

    public void forElementContainText(CSWebElement el, String text) {
        createFluentWait().until(ExpectedConditions.textToBePresentInElementLocated(el.getBy(), text));
    }

    public void forFrameToBeAvailableAndSwitchToIt(String frameId) {
        forFrameToBeAvailableAndSwitchToIt(frameId, DEFAULT_TIMEOUT);
    }

    public void forFrameToBeAvailableAndSwitchToIt(String frameId, Duration timeout) {
        driver.switchTo().defaultContent();

        createFluentWait(timeout, POLLING_INTERVAL)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameId));
    }

    public void forFrameToBeAvailableAndSwitchToIt(int frameId) {
        forFrameToBeAvailableAndSwitchToIt(frameId, DEFAULT_TIMEOUT);
    }

    public void forFrameToBeAvailableAndSwitchToIt(int frameId, Duration timeout) {
        driver.switchTo().defaultContent();

        createFluentWait(timeout, POLLING_INTERVAL)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameId));
    }

    public void changeImplicitWait(Duration duration) {
        driver.manage().timeouts().implicitlyWait(duration);
    }
}
