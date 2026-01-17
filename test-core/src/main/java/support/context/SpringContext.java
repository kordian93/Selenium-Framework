package support.context;

import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static volatile ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) {
        context = ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException("SpringContext not initialized yet.");
        }
        return context.getBean(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}