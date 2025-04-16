package com.misyakuji.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.misyakuji.entity.BorrowerDetails;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {

    private final ResourceLoader resourceLoader;


    private final ObjectMapper objectMapper;

    private final BorrowerDetailsService detailsService;
    /**
     * 统一生成报表逻辑
     *
     * @param inline 是否内联显示（true: 内联显示，false: 下载）
     * @return PDF字节数组
     */
    public ResponseEntity<?> generateReportExample(boolean inline) {

        try {
            // 加载 classpath 资源
            Resource template = resourceLoader.getResource("classpath:templates/debt_bill.jrxml");
            Resource jsonData = resourceLoader.getResource("classpath:templates/debt_bill_data.json");
            // 解析JSON数据
            List<BorrowerDetails> data = objectMapper.readValue(jsonData.getInputStream(), new TypeReference<>() {
            });
            // 生成报表
            byte[] pdfBytes = generateReport(template, data);
            return buildPdfResponse(pdfBytes, "report_example", inline);
        } catch (IOException | JRException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("报表生成失败: " + e.getMessage());
        }

    }

    public ResponseEntity<?> generateReportDatabase() {

        List<BorrowerDetails> detailList = detailsService.getAll();
        if (!detailList.isEmpty()) {

            // 获取报表模板
            Resource template = resourceLoader.getResource("classpath:templates/debt_bill.jrxml");

            byte[] pdfBytes = null;
            try {
                // 生成报表
                pdfBytes = generateReport(template, detailList);
            } catch (IOException | JRException e) {
                return ResponseEntity.internalServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("报表生成失败: " + e.getMessage());
            }
            return buildPdfResponse(pdfBytes, "report_db", true);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * 统一生成报表逻辑
     *
     * @param template   模板资源
     * @param detailList 借贷数据
     * @return PDF字节数组
     */
    public byte[] generateReport(Resource template, List<BorrowerDetails> detailList) throws JRException, IOException {

        // 编译模板并生成报表
        JasperReport jasperReport = JasperCompileManager.compileReport(template.getInputStream());
        // 填充数据
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detailList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
        // 导出为PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }


    /**
     * 构建 PDF 响应
     *
     * @param pdfBytes PDF字节数组
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
}