package com.hp.excel.enhence;

import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.hp.excel.annotation.RequestExcel;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HP
 * @date 2022/11/7
 */
public interface ExcelReaderBuilderEnhance {

    ExcelReaderBuilder enhanceExcel(ExcelReaderBuilder writerBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);

    ExcelReaderSheetBuilder enhanceSheet(ExcelReaderSheetBuilder writerSheetBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);
}
