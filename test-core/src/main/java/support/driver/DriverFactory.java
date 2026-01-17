package support.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import support.config.BrowserStackConfig;
import support.config.CoreConfig;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static support.utils.DateUtils.getTimestamp;

@Log4j2
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver(CoreConfig coreConfig, BrowserStackConfig bsConfig) {
        if (driver.get() == null ||
                (driver.get() instanceof RemoteWebDriver && ((RemoteWebDriver) driver.get()).getSessionId() == null)) {

            if (coreConfig.isUseBrowserstack()) {
                driver.set(createRemoteWebDriver(coreConfig, bsConfig));
                driver.get().manage().window().maximize();
            } else {
                switch (coreConfig.getBrowser().toLowerCase()) {
                    case "firefox" -> {
                        WebDriverManager.firefoxdriver().setup();
                        driver.set(new FirefoxDriver(getFirefoxOptions(coreConfig)));
                    }
                    case "edge" -> {
                        WebDriverManager.edgedriver().setup();
                        driver.set(new EdgeDriver(getEdgeOptions(coreConfig)));
                    }
                    default -> { // Chrome fallback
                        WebDriverManager.chromedriver().setup();
                        driver.set(new ChromeDriver(getChromeOptions(coreConfig)));
                    }
                }
            }

            driver.get()
                    .manage()
                    .timeouts()
                    .implicitlyWait(Duration.ofSeconds(coreConfig.getDefaultImplicitWait()));
        }
        return driver.get();
    }

    public static void quit() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    private static ChromeOptions getChromeOptions(CoreConfig coreConfig) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-search-engine-choice-screen");

        if (coreConfig.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=2560,1440");
            options.addArguments("--disable-gpu");
            options.addArguments("user-agent=" + coreConfig.getHeadlessUserAgent());
        } else {
            options.addArguments("--start-fullscreen");
        }

        options.setAcceptInsecureCerts(true);

        return options;
    }

    private static FirefoxOptions getFirefoxOptions(CoreConfig coreConfig) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--kiosk");

        if (coreConfig.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--window-size=2560,1440");
            options.addArguments("--disable-gpu");
            options.addArguments("user-agent=" + coreConfig.getHeadlessUserAgent());
        }

        return options;
    }

    private static EdgeOptions getEdgeOptions(CoreConfig coreConfig) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-fullscreen");

        if (coreConfig.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--window-size=2560,1440");
            options.addArguments("--disable-gpu");
            options.addArguments("user-agent=" + coreConfig.getHeadlessUserAgent());
        }

        return options;
    }

    @SneakyThrows
    private static RemoteWebDriver createRemoteWebDriver(CoreConfig coreConfig, BrowserStackConfig bsConfig) {
        BrowserStackTunnel.startLocal(bsConfig);

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", bsConfig.getUsername().get());
        bstackOptions.put("accessKey", bsConfig.getAccessKey().get());

        bstackOptions.put("os", bsConfig.getOs());
        bstackOptions.put("osVersion", bsConfig.getOsVersion());

        bstackOptions.put("projectName", bsConfig.getProjectName());
        bstackOptions.put("buildName", "Bitbucket pipeline – " + coreConfig.getBrowser() + getTimestamp());
        bstackOptions.put("buildIdentifier", "iden-" + getTimestamp());

        bstackOptions.put("idleTimeout", "300");
        bstackOptions.put("networkLogs", "true");

        ChromeOptions options = new ChromeOptions();
        options.setCapability("bstack:options", bstackOptions);
        options.setBrowserVersion("latest");

        String hubUrl = "https://hub-cloud.browserstack.com/wd/hub";
        return new RemoteWebDriver(URI.create(hubUrl).toURL(), options);
    }
}
