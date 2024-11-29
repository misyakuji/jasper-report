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

    /**
     * 统一生成报表逻辑
     *
     * @param templateResource 模板资源
     * @param jsonResource     JSON数据资源
     * @return PDF字节数组
     */
    public byte[] generateReport(Resource templateResource, Resource jsonResource) throws JRException, IOException {
        // 解析JSON数据
        List<ReportEntity> data = objectMapper.readValue(jsonResource.getInputStream(), new TypeReference<>() {});

        // 编译模板并生成报表
        JasperReport jasperReport = JasperCompileManager.compileReport(templateResource.getInputStream());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

}