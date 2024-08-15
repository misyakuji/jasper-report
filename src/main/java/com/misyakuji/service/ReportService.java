package com.misyakuji.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.misyakuji.entity.ReportEntity;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    public byte[] generateReport(List<ReportEntity> reports) throws JRException, IOException {
        Resource template = resourceLoader.getResource("classpath:templates/debt_bill.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(template.getInputStream());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reports);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] generateReportExample() throws JRException, IOException {
        Resource json = resourceLoader.getResource("classpath:templates/debt_bill_data.json");
        List<ReportEntity> exampleData = objectMapper.readValue(json.getInputStream(), new TypeReference<>() {});
        return generateReport(exampleData);
    }
}
