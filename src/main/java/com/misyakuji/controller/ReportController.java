package com.misyakuji.controller;

import com.misyakuji.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 使用 classpath 模板和 JSON 数据生成示例报表
     * GET http://localhost:8088/api/reports/bill/example?inline=true
     *
     * @param inline 是否内联显示（true: 内联显示，false: 下载）
     * @return ResponseEntity pdf报表
     */
    @GetMapping(value = "/bill/example", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateReportExample(@RequestParam(required = false, defaultValue = "true") boolean inline) {
        return reportService.generateReportExample(inline);
    }

    /**
     * 以数据库为数据源生成报表
     * Get http://localhost:8088/api/reports/bill/db
     *
     * @return ResponseEntity pdf报表
     */
    @GetMapping(value = "/bill/db", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateReportDatabase() {
        return reportService.generateReportDatabase();
    }
}