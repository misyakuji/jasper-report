package com.misyakuji.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    @Value("${app.database.path}")
    private String dbPath;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            File parentDir = new File(dbPath).getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                logger.info("Creating database directory: {}", parentDir.getAbsolutePath());
            }
//            executeDDL("classpath:ddl/borrowers.sql");
//            executeDDL("classpath:ddl/borrower_details.sql");
        } catch (Exception e) {
            logger.error("Database initialize failed!", e);
        }
    }

    private void executeDDL(String resourcePath) {
        try {
            Resource resource = resourceLoader.getResource(resourcePath);

            if (!resource.exists()) {
                throw new IOException("DDL script not found: " + resourcePath);
            }
            var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

            String sql = FileCopyUtils.copyToString(reader);
            jdbcTemplate.execute(sql);
            logger.info("Executed DDL: {}", resource.getFilename());
        } catch (Exception e) {
            logger.error("DDL execution failed: {}", resourcePath, e);
        }
    }
}