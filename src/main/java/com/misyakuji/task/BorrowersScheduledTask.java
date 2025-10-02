package com.misyakuji.task;

import com.misyakuji.service.BorrowersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 借款人信息定时更新任务
 * 每天执行一次，自动更新所有借款人的财务信息
 */
@Component
public class BorrowersScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(BorrowersScheduledTask.class);
    private final BorrowersService borrowersService;

    public BorrowersScheduledTask(BorrowersService borrowersService) {
        this.borrowersService = borrowersService;
    }

    /**
     * 每天凌晨0点执行一次，自动更新所有借款人的财务信息
     * cron表达式含义：秒 分 时 日 月 星期
     * 0 0 0 * * ? 表示每天0点0分0秒执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoUpdateAllBorrowers() {
        logger.info("开始执行所有借款人信息自动更新任务");
        try {
            borrowersService.calculatorAll();
            logger.info("所有借款人信息自动更新任务执行完成");
        } catch (Exception e) {
            logger.error("执行借款人信息自动更新任务时发生系统错误: {}", e.getMessage(), e);
        }
    }
}