package support.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "jasypt.encryptor")
public class EncryptorProperties {

    @NotEmpty
    private String password;

    private String algorithm = "PBEWithMD5AndDES";
    private String providerName = "SunJCE";
    private int keyObtentionIterations = 1000;
    private int poolSize = 1;
    private String saltGeneratorClassName = "org.jasypt.salt.RandomSaltGenerator";
    private String stringOutputType = "base64";
}
