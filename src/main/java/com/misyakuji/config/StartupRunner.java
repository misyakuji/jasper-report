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

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${app.auto-open:false}")
    private boolean autoOpenFlag;

    @Override
    public void run(ApplicationArguments args) {
        String url = String.format("http://%s:%d%s/reports/bill/example?inline=true", serverAddress, serverPort, contextPath);
        logger.info("See example {}", url);

        if (autoOpenFlag) {
            // 启动后自动打开链接
             openBrowser(url);
        }
    }

    private boolean isDesktopEnvironment() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") || os.contains("nix") || os.contains("nux") || os.contains("mac");
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String[] command;
            if (os.contains("win")) {
                command = new String[]{"cmd", "/c", "start", url};
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                command = new String[]{"xdg-open", url};
            } else {
                logger.warn("Unsupported operating system: {}", os);
                return;
            }
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO(); // 继承当前进程的输入输出流
            processBuilder.start(); // 启动进程
        } catch (IOException e) {
            logger.error("Error when automatically opening the browser", e);
        }
    }
}
