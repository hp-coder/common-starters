package com.luban.excel.enhence;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.luban.excel.annotation.ResponseExcel;
import com.luban.excel.head.HeadGenerator;

import javax.servlet.http.HttpServletResponse;

/**
 * @author HP
 * @date 2022/11/7
 */
public interface ExcelWriterBuilderEnhance {

    ExcelWriterBuilder enhanceExcel(ExcelWriterBuilder writerBuilder, HttpServletResponse response, ResponseExcel responseExcel, Class<?> dataClass, String templatePath);

    ExcelWriterSheetBuilder enhanceSheet(ExcelWriterSheetBuilder writerSheetBuilder, Integer sheetNo, String sheetName, Class<?> dataClass, String template, Class<? extends HeadGenerator> headEnhancerClass);
}
