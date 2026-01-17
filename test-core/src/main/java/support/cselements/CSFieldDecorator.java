package support.cselements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import support.base.BaseComponent;
import support.context.ContextHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class CSFieldDecorator implements FieldDecorator {
    private final BaseComponent parentPage;

    public CSFieldDecorator(Object parentPage) {
        this.parentPage = (BaseComponent) parentPage;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if (WebElement.class.isAssignableFrom(field.getType())) {
            return createCustomElement(loader, field);
        }
        return null;
    }

    private Object createCustomElement(ClassLoader loader, Field field) {
        ElementLocator locator = new CSElementLocatorFactory(ContextHolder.get().getDriver()).createLocator(field);
        if (locator == null) {
            return null;
        }

        By by = ((CSElementLocator) locator).getBy();

        WebElement proxy = (WebElement) Proxy.newProxyInstance(
                loader,
                new Class[]{WebElement.class},
                new LocatingElementHandler(locator)
        );

        return switch (field.getType().getName()) {
            case "support.cselements.CSWebElement" -> new CSWebElement(proxy, parentPage, by);
            case "support.cselements.CSSelect" -> new CSSelect(proxy, parentPage, by);
            default -> throw new IllegalArgumentException("Unsupported WebElement subclass: " + field.getType());
        };
    }
}
