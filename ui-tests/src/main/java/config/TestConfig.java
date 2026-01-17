package config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import support.masking.MaskedString;

import static support.utils.ValidationUtils.isValidUrl;

@Data
@ConfigurationProperties(prefix = "test.config")
@Log4j2
public class TestConfig {

    private String baseUrl;

    @PostConstruct
    public void validate() {
        if (!isValidUrl(baseUrl)) {
            log.error("'baseUrl' is invalid");
            throw new IllegalArgumentException("'baseUrl' is invalid");
        }
    }
}
