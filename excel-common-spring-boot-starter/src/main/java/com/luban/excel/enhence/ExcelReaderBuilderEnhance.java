package com.luban.excel.enhence;

import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.luban.excel.annotation.RequestExcel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author hp
 * @date 2022/11/7
 */
public interface ExcelReaderBuilderEnhance {

    ExcelReaderBuilder enhanceExcel(ExcelReaderBuilder writerBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);

    ExcelReaderSheetBuilder enhanceSheet(ExcelReaderSheetBuilder writerSheetBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);
}
