package com.misyakuji.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * MariaDB数据库初始化器
 * MariaDB不需要文件系统路径管理，DDL由spring.sql.init配置处理
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("MariaDB database initialization handled by Spring Boot SQL initialization");
        // MariaDB的表结构和数据初始化已由application.yml中的spring.sql.init配置处理
        // 不需要手动创建目录或执行DDL脚本
    }
}