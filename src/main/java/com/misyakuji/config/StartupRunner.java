package com.misyakuji.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StartupRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Value("${server.address:127.0.0.1}")
    private String serverAddress;

    @Value("${server.port:8080}")
    private int serverPort;

    @Override
    public void run(ApplicationArguments args) {
        String url = String.format("http://%s:%d/api/v1/reports/bill_example?type=show", serverAddress, serverPort);
        logger.info("See example {}", url);

        if (isDesktopEnvironment()) {
            // openBrowser(url);
        }
    }

    private boolean isDesktopEnvironment() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") || os.contains("nix") || os.contains("nux") || os.contains("mac");
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("cmd /c start " + url);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                logger.warn("Unsupported operating systems: {}", os);
            }
        } catch (IOException e) {
            logger.error("Error when automatically opening the browser", e);
        }
    }
}
