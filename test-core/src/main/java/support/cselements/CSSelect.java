package support.cselements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import support.base.BaseComponent;

import java.util.List;

public class CSSelect extends CSWebElement {

    public CSSelect(WebElement element, BaseComponent parentPage, By locator) {
        super(element, parentPage, locator);
    }

    public void selectByValue(String value) {
        addReproStep("Select value: '" + value + "' on: " + getBy());

        Select select = new Select(element);
        wait.forElementVisible(this);
        wait.forElementClickable(this);
        select.selectByValue(value);
    }

    public void selectByIndex(int index) {
        addReproStep("Select index: '" + index + "' on: " + getBy());

        Select select = new Select(element);
        select.selectByIndex(index);
    }

    public void selectByVisibleText(String text) {
        addReproStep("Select text: '" + text + "' on: " + getBy());

        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public void deselectAll() {
        addReproStep("Deselect all: " + getBy());

        Select select = new Select(element);
        select.deselectAll();
    }

    public void deselectByIndex(int index) {
        addReproStep("Deselect index: '" + index + "' on: " + getBy());

        Select select = new Select(element);
        select.deselectByIndex(index);
    }

    public void deselectByValue(String value) {
        addReproStep("Deselect value: '" + value + "' on: " + getBy());

        Select select = new Select(element);
        select.deselectByValue(value);
    }

    public void deselectByVisibleText(String text) {
        addReproStep("Deselect text: '" + text + "' on: " + getBy());

        Select select = new Select(element);
        select.deselectByVisibleText(text);
    }

    public List<WebElement> getAllSelectedOptions() {
        Select select = new Select(element);
        return select.getAllSelectedOptions();
    }

    public WebElement getFirstSelectedOption() {
        Select select = new Select(element);
        return select.getFirstSelectedOption();
    }

    public List<WebElement> getOptions() {
        Select select = new Select(element);
        return select.getOptions();
    }

    public boolean isMultiple() {
        Select select = new Select(element);
        return select.isMultiple();
    }
}
