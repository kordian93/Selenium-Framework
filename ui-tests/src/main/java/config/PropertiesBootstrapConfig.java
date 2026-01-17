package config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"config", "support.config", "support.context", "utils", "data"})
@ConfigurationPropertiesScan(basePackages = {"config", "support.config", "support.context", "utils", "data"})
public class PropertiesBootstrapConfig {
}
