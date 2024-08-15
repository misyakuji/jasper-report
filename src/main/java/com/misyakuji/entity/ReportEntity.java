package com.misyakuji.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class ReportEntity {
    private String date;
    private BigDecimal amount;
    private String remark;
    private String timestamp;

    public ReportEntity() {
        this.timestamp = LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
