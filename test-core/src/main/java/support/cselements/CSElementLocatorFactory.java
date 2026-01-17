package support.cselements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public class CSElementLocatorFactory implements ElementLocatorFactory {

    private final WebDriver driver;

    public CSElementLocatorFactory(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new CSElementLocator(driver, field);
    }
}