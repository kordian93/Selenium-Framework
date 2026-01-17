package support.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import support.masking.MaskedString;

@Data
@ConfigurationProperties(prefix = "browserstack")
public class BrowserStackConfig {

    private MaskedString username;
    private MaskedString accessKey;
    private String os;
    private String osVersion;
    private String projectName;

    private String browserName;
    private String browserVersion;
}
