package com.luban.excel.enhence;

import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.luban.excel.annotation.RequestExcel;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HP
 * @date 2022/11/8
 */
public class MergeRowReaderEnhance implements ExcelReaderBuilderEnhance {
    @Override
    public ExcelReaderBuilder enhanceExcel(ExcelReaderBuilder writerBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass) {
        return writerBuilder.extraRead(CellExtraTypeEnum.MERGE);
    }

    @Override
    public ExcelReaderSheetBuilder enhanceSheet(ExcelReaderSheetBuilder writerSheetBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass) {
        return writerSheetBuilder;
    }
}
