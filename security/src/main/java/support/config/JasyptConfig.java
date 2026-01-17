package support.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EncryptorProperties.class)
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(EncryptorProperties props) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(props.getPassword());
        config.setAlgorithm(props.getAlgorithm());
        config.setKeyObtentionIterations(String.valueOf(props.getKeyObtentionIterations()));
        config.setPoolSize(String.valueOf(props.getPoolSize()));
        config.setProviderName(props.getProviderName());
        config.setSaltGeneratorClassName(props.getSaltGeneratorClassName());
        config.setStringOutputType(props.getStringOutputType());

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }
}

