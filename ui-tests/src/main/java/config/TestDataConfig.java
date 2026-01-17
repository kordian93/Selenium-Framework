package config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import support.masking.MaskedString;

@Data
@EnableEncryptableProperties
@ConfigurationProperties(prefix = "test.data")
@Log4j2
public class TestDataConfig {

}
