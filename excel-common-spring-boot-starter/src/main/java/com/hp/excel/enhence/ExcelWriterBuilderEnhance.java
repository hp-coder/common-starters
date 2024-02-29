package com.hp.excel.enhence;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.head.HeadGenerator;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hp
 * @date 2022/11/7
 */
public interface ExcelWriterBuilderEnhance {

    ExcelWriterBuilder enhanceExcel(ExcelWriterBuilder writerBuilder, HttpServletResponse response, ResponseExcel responseExcel, Class<?> dataClass, String templatePath);

    ExcelWriterSheetBuilder enhanceSheet(ExcelWriterSheetBuilder writerSheetBuilder, Integer sheetNo, String sheetName, Class<?> dataClass, String template, Class<? extends HeadGenerator> headEnhancerClass);
}
