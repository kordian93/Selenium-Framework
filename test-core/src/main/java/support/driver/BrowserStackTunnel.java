package support.driver;

import com.browserstack.local.Local;
import lombok.extern.log4j.Log4j2;
import support.config.BrowserStackConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class BrowserStackTunnel {

    private static Local local;
    private static final Object lock = new Object();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;

                if (os.contains("win")) {   // Windows cleanup
                    pb = new ProcessBuilder("taskkill", "/F", "/IM", "BrowserStackLocal.exe", "/T");
                } else {    // Linux/macOS cleanup
                    pb = new ProcessBuilder("pkill", "-f", "BrowserStackLocal");
                }

                pb.inheritIO().start().waitFor();

            } catch (Exception e) {
                log.warn("Failed to kill BrowserStackLocal processes at shutdown: {}", e.getMessage());
            }
        }));
    }

    public static void startLocal(BrowserStackConfig bsConfig) throws Exception {
        synchronized (lock) {
            if (local != null && (local.isRunning() || isLocalRunning())) {
                return;
            }

            try {
                new ProcessBuilder("taskkill", "/F", "/IM", "BrowserStackLocal.exe", "/T")
                        .inheritIO()
                        .start()
                        .waitFor();
            } catch (Exception e) {
            }

            local = new Local();
            Map<String, String> args = new HashMap<>();
            args.put("key", bsConfig.getAccessKey().get());

            local.start(args);

            int retries = 0;
            while (!local.isRunning() && retries < 10) {
                Thread.sleep(1000);
                retries++;
            }

            if (!local.isRunning()) {
                throw new IllegalStateException("BrowserStackLocal failed to start properly after 10s");
            }
        }
    }

    public static void stopLocal() throws Exception {
        synchronized (lock) {
            if (local != null && local.isRunning()) {
                try {
                    local.stop();
                } catch (Exception e) {
                    log.warn("Failed to stop BrowserStackLocal", e);
                }
                local = null;
            }
        }
    }

    private static boolean isLocalRunning() {
        try {
            Process process = new ProcessBuilder("tasklist").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().anyMatch(line -> line.contains("BrowserStackLocal.exe"));
            }
        } catch (Exception e) {
            return false;
        }
    }
}
