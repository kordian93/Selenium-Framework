package support.base;

import org.openqa.selenium.WebDriver;
import support.config.BrowserStackConfig;
import support.config.CoreConfig;
import support.context.ContextHolder;
import support.context.TestContext;
import support.driver.DriverFactory;
import support.driver.PageFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface IBaseTest {

    ThreadLocal<PageFactory> pageFactory = new ThreadLocal<>();
    Map<Class<? extends BaseComponent>, ThreadLocal<? extends BaseComponent>> registry = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    default <T extends BaseComponent> T getPage(Class<T> clazz) {
        return (T) registry
                .computeIfAbsent(clazz, f -> ThreadLocal.withInitial(() -> createPage(clazz)))
                .get();
    }

    default <T extends BaseComponent> T createPage(Class<T> pageClass) {
        if (pageFactory.get() == null) {
            pageFactory.set(new PageFactory());
        }
        return pageFactory.get().create(pageClass);
    }

    default void initContext(CoreConfig coreConfig, BrowserStackConfig bsConfig) {
        WebDriver driver = DriverFactory.getDriver(coreConfig, bsConfig);

        TestContext context = new TestContext(driver, coreConfig);
        ContextHolder.set(context);

        PageFactory pf = new PageFactory();
        pageFactory.set(pf);
    }

    default void clearContext() {
        clearThreadLocals();
        pageFactory.remove();
        DriverFactory.quit();
        ContextHolder.clear();
    }

    default void clearThreadLocals() {
        registry.values().forEach(ThreadLocal::remove);
    }
}
