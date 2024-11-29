package com.misyakuji.controller;

import com.misyakuji.service.ReportService;
import lombok.Data;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/" + ReportController.VERSION + ReportController.RESOURCE_NAME)
public class ReportController {

    public static final String RESOURCE_NAME = "/reports";
    public static final String VERSION = "v1";

    @Autowired
    private ReportService reportService;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 使用 classpath 模板和 JSON 数据生成示例报表
     * GET http://localhost:8088/api/v1/reports/bill/example?inline=true
     *
     * @param inline 是否内联显示（true: 内联显示，false: 下载）
     * @return PDF 文件响应
     */
    @GetMapping(value = "/bill/example", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateReportExample(@RequestParam(required = false, defaultValue = "true") boolean inline) {
        try {
            // 加载 classpath 资源
//            Resource template = resourceLoader.getResource("classpath:templates/debt_bill.jrxml");
//            Resource jsonData = resourceLoader.getResource("classpath:templates/debt_bill_data.json");

            Resource template = resourceLoader.getResource("https://raw.githubusercontent.com/Misyakuji/my-files/refs/heads/master/jrxml/debt_bill.jrxml");
            Resource jsonData = resourceLoader.getResource("https://raw.githubusercontent.com/Misyakuji/my-files/refs/heads/master/json/debt_bill_data-new.json");

            // 生成报表
            byte[] pdfBytes = reportService.generateReport(template, jsonData);
            return buildPdfResponse(pdfBytes, "report_example", inline);
        } catch (IOException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("报表生成失败: " + e.getMessage());
        }
    }

    /**
     * 从 Web URL 加载模板和 JSON 数据生成报表
     * POST http://localhost:8088/api/v1/reports/bill/web
     *
     * @param request 包含模板 URL 和 JSON URL 的请求体
     * @return PDF 文件响应或错误信息
     */
    @PostMapping(value = "/bill/web", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateReportFromWeb(@RequestBody WebResourceRequest request) {
        try {
            // 加载 Web 资源
            Resource template = resourceLoader.getResource(request.getTemplateUrl());
            Resource jsonData = resourceLoader.getResource(request.getJsonUrl());

            // 生成报表
            byte[] pdfBytes = reportService.generateReport(template, jsonData);
            return buildPdfResponse(pdfBytes, "report_web", request.isInline());
        } catch (IOException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("报表生成失败: " + e.getMessage());
        }
    }

    /**
     * 构建 PDF 响应
     *
     * @param pdfBytes PDF 字节数组
     * @param prefix   文件名前缀
     * @param inline   是否内联显示
     * @return PDF 文件响应
     */
    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String prefix, boolean inline) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // 生成文件名
        String filename = prefix + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ".pdf";

        // 设置 Content-Disposition
        ContentDisposition disposition = inline ?
                ContentDisposition.inline().filename(filename).build() :
                ContentDisposition.attachment().filename(filename).build();
        headers.setContentDisposition(disposition);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    /**
     * Web 资源请求体
     */
    @Data
    static class WebResourceRequest {
        /**
         * 报表模板 URL
         */
        private String templateUrl;

        /**
         * 报表 JSON 数据源 URL
         */
        private String jsonUrl;

        /**
         * 是否内联显示（true: 内联显示，false: 下载）
         */
        private boolean inline = true;
    }
}