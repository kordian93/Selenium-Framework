package support.cselements;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.util.List;

public class CSElementLocator implements ElementLocator {
    private final WebDriver driver;

    @Getter
    private final By by;

    public CSElementLocator(WebDriver driver, Field field) {
        this.driver = driver;
        this.by = createByFromField(field);
    }

    @Override
    public WebElement findElement() {
        return driver.findElement(by);
    }

    @Override
    public List<WebElement> findElements() {
        return driver.findElements(by);
    }

    private By createByFromField(Field field) {
        FindBy findBy = field.getAnnotation(FindBy.class);
        if (findBy != null) {
            if (!findBy.css().isEmpty()) {
                return By.cssSelector(findBy.css());
            }
            if (!findBy.xpath().isEmpty()) {
                return By.xpath(findBy.xpath());
            }
            if (!findBy.id().isEmpty()) {
                return By.id(findBy.id());
            }

            throw new IllegalArgumentException("No valid locator strategy found in @FindBy annotation");
        }
        throw new IllegalArgumentException("Field must have @FindBy annotation");
    }
}
