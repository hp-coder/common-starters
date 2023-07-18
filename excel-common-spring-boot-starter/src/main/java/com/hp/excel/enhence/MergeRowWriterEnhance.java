package com.hp.excel.enhence;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.head.HeadGenerator;
import com.hp.excel.enhence.handler.ContentBasedDynamicMergeHandler;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HP
 * @date 2022/11/8
 */
public class MergeRowWriterEnhance implements ExcelWriterBuilderEnhance {
    @Override
    public @Nonnull
    ExcelWriterBuilder enhanceExcel(ExcelWriterBuilder writerBuilder, HttpServletResponse response, ResponseExcel responseExcel, Class<?> dataClass, String templatePath) {
        return writerBuilder.registerWriteHandler(new ContentBasedDynamicMergeHandler(dataClass));
    }

    @Override
    public @Nonnull
    ExcelWriterSheetBuilder enhanceSheet(ExcelWriterSheetBuilder writerSheetBuilder, Integer sheetNo, String sheetName, Class<?> dataClass, String template, Class<? extends HeadGenerator> headEnhancerClass) {
        return writerSheetBuilder;
    }
}
