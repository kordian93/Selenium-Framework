package support.crypto;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import support.config.JasyptConfig;

public class EncryptorTool {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: mvn exec:java -Djasypt.encryptor.password=\"passwordForEncryption\"-Dexec.args=\"valueToEncrypt\"");
            System.exit(1);
        }

        String valueToEncrypt = args[0];

        SpringApplication app = new SpringApplication(JasyptConfig.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        try (ConfigurableApplicationContext context = app.run()) {
            StringEncryptor encryptor = context.getBean("jasyptStringEncryptor", StringEncryptor.class);
            String encrypted = encryptor.encrypt(valueToEncrypt);
            System.out.println("Encrypted: ENC(" + encrypted + ")");
        }
    }
}

