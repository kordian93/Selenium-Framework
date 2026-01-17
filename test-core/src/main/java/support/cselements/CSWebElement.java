package support.cselements;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import support.annotations.StringOrMasked;
import support.base.BaseComponent;
import support.context.ContextHolder;
import support.exceptions.ElementNotPresentException;
import support.masking.MaskedString;
import support.utils.Wait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class CSWebElement implements WebElement {

    @Getter
    protected final WebElement element;
    protected final WebDriver driver;
    protected final Wait wait;
    protected final JavascriptExecutor js;
    protected final Actions actions;
    protected final BaseComponent parentPage;

    @Getter
    protected final By by;

    public CSWebElement(By by, BaseComponent parentPage) {
        this(resolveElement(by, parentPage), parentPage, by);
    }

    public CSWebElement(WebElement element, BaseComponent parentPage) {
        this(element, parentPage, null);
    }

    public CSWebElement(WebElement element, BaseComponent page, By by) {
        this.driver = ContextHolder.get().getDriver();
        this.js = ContextHolder.get().getJs();
        this.actions = ContextHolder.get().getActions();
        this.wait = ContextHolder.get().getWait();
        this.element = element;
        this.parentPage = page;
        this.by = by;
    }

    private static WebElement resolveElement(By by, BaseComponent parentPage) {
        WebDriver driver = ContextHolder.get().getDriver();

        try {
            return driver.findElement(by);
        } catch (NoSuchElementException e) {
            parentPage.waitForLoaders();
            return driver.findElement(by);
        }
    }

    public static List<CSWebElement> findAll(By by, BaseComponent parentPage) {
        if (ContextHolder.get().getDriver().findElements(by).isEmpty()) {
            return Collections.emptyList();
        }

        CSWebElement prototype = new CSWebElement(by, parentPage);
        return findAll(prototype, parentPage);
    }

    public static List<CSWebElement> findAll(CSWebElement element, BaseComponent parentPage) {
        if (ContextHolder.get().getDriver().findElements(element.getBy()).isEmpty()) {
            return Collections.emptyList();
        }

        ContextHolder.get().getWait().forElementVisible(element, Duration.ofSeconds(2));

        return ContextHolder.get().getDriver().findElements(element.getBy()).stream()
                .map(el -> new CSWebElement(el, parentPage, element.getBy()))
                .collect(Collectors.toList());
    }

    public static CSWebElement find(CSWebElement element, BaseComponent parentPage, int index) {
        return findAll(element, parentPage).get(index);
    }

    public static CSWebElement find(By locator, BaseComponent parentPage, int index) {
        return findAll(locator, parentPage).get(index);
    }

    public void clickWhenReady() {
        parentPage.waitForLoaders();
        try {
            wait.forElementVisible(this);
            wait.forElementClickable(this);
        } catch (TimeoutException e) {
            log.debug("Element not ready to be clicked >>> {}\nReason >>> {}\nTrying to recover...", by, e.getClass().getSimpleName());
        }
        click();
    }

    @Override
    public void click() {
        addReproStep("Click: " + by);

        try {
            element.click();
        } catch (TimeoutException | ElementClickInterceptedException | StaleElementReferenceException e) {
            log.debug("Element not clicked >>> {}\nReason >>> {}\nTrying to recover...", by, e.getClass().getSimpleName());
            try {
                tryClickWithRecovery();
            } catch (TimeoutException | ElementClickInterceptedException | StaleElementReferenceException e2) {
                log.debug("Element not clicked >>> {}\nReason >>> {}\nTrying to recover last time with new element...", getBy(), e2.getClass().getSimpleName());
                parentPage.waitForLoaders();
                CSWebElement lastElement = new CSWebElement(by, parentPage);
                wait.forElementVisible(lastElement);
                wait.forElementClickable(lastElement);
                driver.findElement(by).click();
            }
        }
    }

    private void tryClickWithRecovery() {
        PageFactory.initElements(new CSFieldDecorator(parentPage), element.getClass());
        if (!wait.isPresent(this, wait.getDEFAULT_TIMEOUT())) {
            throw new ElementNotPresentException();
        }
        this.scrollIntoView();
        wait.forElementClickable(this);
        element.click();
    }

    @Override
    public void submit() {
        addReproStep("Submit: " + getBy());

        element.submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        if (keysToSend.length == 0) {
            return;
        }
        element.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        addReproStep("Clear: " + getBy());

        wait.forElementClickable(this);
        element.clear();
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        wait.forElementVisible(this);
        String value = element.getDomAttribute(name);
        return value != null ? value : element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        parentPage.waitForLoaders();
        wait.forElementVisible(this);
        String text = element.getText();
        return text;
    }

    @Override
    public WebElement findElement(By by) {
        WebElement childElement = element.findElement(by);
        return new CSWebElement(childElement, parentPage, this.by);
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> childElements = element.findElements(by);
        return childElements.stream()
                .map(childElement -> new CSWebElement(childElement, parentPage, this.by))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDisplayed() {
        return isDisplayed(wait.getDEFAULT_TIMEOUT());
    }

    public boolean isDisplayed(Duration timeout) {
        try {
            return wait.isPresent(this, timeout) && wait.forElementVisible(this).isDisplayed();
        } catch (NullPointerException e) {
            log.warn("{} returned NullPointerException on isDisplayed check.", this.element);
            return false;
        }
    }

    public boolean isNotDisplayed() {
        try {
            return wait.forElementNotVisible(this);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return element.getScreenshotAs(target);
    }

    public CSWebElement scrollIntoView() {
        // Scrolls to element and 300px up to make sure it's not obscured by sticky headers
        wait.forElementVisible(this);
        js.executeScript("arguments[0].scrollIntoView(true); window.scrollBy(0, -300);", driver.findElement(getBy()));
        return this;
    }

    public void copyPasteInput(@StringOrMasked Object value) {
        addReproStep("Copy-paste: '" + value + "' into: " + getBy());

        if (value == null) {
            return;
        }
        if (value instanceof MaskedString masked) {
            value = masked.get();
        }

        if (value instanceof CharSequence keysToSend) {
            if (keysToSend.isEmpty()) {
                return;
            }
            WebElement input = driver.findElement(getBy());
            js.executeScript("arguments[0].value = arguments[1];", input, value);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input);

            click();
            actions.keyDown(Keys.CONTROL)
                    .sendKeys("a")
                    .sendKeys("c")
                    .sendKeys("v")
                    .keyUp(Keys.CONTROL)
                    .perform();
        } else {
            throw new IllegalArgumentException("You can only use String or MaskedString here. Class used: " + value.getClass());
        }
    }

    public void clearAndSendKeys(@StringOrMasked Object value) {
        wait.forPageToLoad();
        clear();
        safeSendKeys(value);
    }

    public void safeSendKeys(@StringOrMasked Object value) {
        addReproStep("Type: '" + value + "' into: " + getBy());
        parentPage.waitForLoaders();

        if (value == null) {
            return;
        }
        if (value instanceof MaskedString masked) {
            value = masked.get();
        }
        if (value instanceof Integer) {
            value = String.valueOf(value);
        }

        if (value instanceof CharSequence keysToSend) {
            if (keysToSend.isEmpty()) {
                return;
            }

            wait.forElementClickable(this);
            element.sendKeys(keysToSend);
        } else {
            throw new IllegalArgumentException("You can only use String or MaskedString here. Class used: " + value.getClass());
        }
    }

    protected void addReproStep(String message) {
        ContextHolder.get().getLastReproSteps().addStep(message + " <|> " + parentPage.getClass().getSimpleName());
    }
}
