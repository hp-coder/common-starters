package com.luban.excel;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.alibaba.excel.converters.Converter;
import com.luban.excel.aop.ResponseExcelReturnValueHandler;
import com.luban.excel.handler.ExcelSheetWriteHandler;
import com.luban.excel.handler.MultiSheetWriteHandler;
import com.luban.excel.handler.SingleSheetWriteHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author hp
 * @date 2022/11/7
 */
@EnableSpringUtil
@RequiredArgsConstructor
public class ExcelHandlerConfiguration {
    private final ObjectProvider<List<Converter<?>>> converterProvider;

//    @Bean
//    @ConditionalOnMissingBean
//    public WriterBuilderEnhancer writerBuilderEnhancer() {
//        return new DefaultWriterBuilderEnhancer();
//    }

    @Bean
    @ConditionalOnMissingBean
    public SingleSheetWriteHandler singleSheetWriteHandler() {
        return new SingleSheetWriteHandler(this.converterProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public MultiSheetWriteHandler manySheetWriteHandler() {
        return new MultiSheetWriteHandler(this.converterProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseExcelReturnValueHandler responseExcelReturnValueHandler(List<ExcelSheetWriteHandler> sheetWriteHandlerList) {
        return new ResponseExcelReturnValueHandler(sheetWriteHandlerList);
    }
//
//    @Bean
//    @ConditionalOnBean({MessageSource.class})
//    @ConditionalOnMissingBean
//    public I18nHeaderCellWriteHandler i18nHeaderCellWriteHandler(MessageSource messageSource) {
//        return new I18nHeaderCellWriteHandler(messageSource);
//    }
}
