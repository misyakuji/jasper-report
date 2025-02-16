package com.misyakuji.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DatabaseFolderCreationListener implements ApplicationListener<ApplicationStartingEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseFolderCreationListener.class);

    @Value("${app.database.path}")
    private String dbPath;

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        File parentDir = new File(dbPath).getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                logger.info("Successfully created database directory: {}", parentDir.getAbsolutePath());
            } else {
                logger.error("Failed to create database directory: {}", parentDir.getAbsolutePath());
            }
        }
    }
}