package support.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import support.context.SpringContext;

@Data
@ConfigurationProperties(prefix = "core.config")
@Log4j2
public class CoreConfig {

    private String browser;
    private boolean headless;
    private boolean repeats;
    private boolean useBrowserstack;
    private String headlessUserAgent;
    private int defaultTimeout;
    private int defaultImplicitWait;

    public String getProfile() {
        Environment env = SpringContext.getApplicationContext().getBean(Environment.class);
        return env.getProperty("spring.profiles.active");
    }
}
