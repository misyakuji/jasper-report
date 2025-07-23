package com.misyakuji.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class TriggerInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TriggerInitializer.class);

    private final DataSource dataSource;

    // 注入数据源
    public TriggerInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        // 手动执行触发器脚本
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // 加载触发器脚本
        populator.addScript(new org.springframework.core.io.ClassPathResource("ddl/triggers.sql"));
        // 明确指定分隔符为;;（与脚本中的结尾匹配）
        populator.setSeparator(";;");
        // 执行脚本（在表结构创建后）
        try (Connection conn = dataSource.getConnection()) {
            populator.populate(conn);
            logger.info("触发器添加成功...");
        } catch (Exception e) {
            logger.error("触发器添加失败...");
            throw e;
        }
    }
}
