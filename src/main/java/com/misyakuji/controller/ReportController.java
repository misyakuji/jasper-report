package com.misyakuji.controller;

import com.misyakuji.entity.ReportEntity;
import com.misyakuji.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/" + ReportController.VERSION + ReportController.RESOURCE_NAME)
public class ReportController {

    public static final String RESOURCE_NAME = "/reports";

    public static final String VERSION = "v1";
    @Autowired
    private ReportService reportService;

    // http://localhost:8088/api/v1/reports/bill
    @PostMapping(value = {"/bill"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReport(@RequestBody List<ReportEntity> reports) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "report_" + timestamp + ".pdf";
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(reportService.generateReport(reports));
    }

    // http://localhost:8088/api/v1/reports/bill_example?type=show
    @GetMapping(value = {"/bill_example"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReportExample(@RequestParam(required = false) String type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "report_" + timestamp + ".pdf";
        if ("show".equals(type)) {
            headers.setContentDisposition(ContentDisposition.inline().filename(filename).build());
        } else {
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        }
        return ResponseEntity.ok().headers(headers).body(reportService.generateReportExample());
    }
}
