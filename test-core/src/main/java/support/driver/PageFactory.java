package support.driver;

import support.base.BaseComponent;

import java.lang.reflect.Constructor;

public class PageFactory {

    public <T extends BaseComponent> T create(Class<T> pageClass) {
        try {
            Constructor<T> constructor = pageClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create page object for: " + pageClass.getSimpleName(), e);
        }
    }
}