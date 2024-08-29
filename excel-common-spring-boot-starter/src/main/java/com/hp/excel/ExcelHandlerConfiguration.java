package com.hp.excel;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.alibaba.excel.converters.Converter;
import com.hp.excel.aop.ResponseExcelReturnValueHandler;
import com.hp.excel.handler.MultiSheetWriteHandler;
import com.hp.excel.handler.SingleSheetWriteHandler;
import com.hp.excel.handler.ExcelSheetWriteHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author hp
 */
@EnableSpringUtil
@RequiredArgsConstructor
public class ExcelHandlerConfiguration {

    private final ObjectProvider<List<Converter<?>>> converterProvider;

    @Bean
    @ConditionalOnMissingBean
    public SingleSheetWriteHandler singleSheetWriteHandler() {
        return new SingleSheetWriteHandler(this.converterProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public MultiSheetWriteHandler multiSheetWriteHandler() {
        return new MultiSheetWriteHandler(this.converterProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseExcelReturnValueHandler responseExcelReturnValueHandler(List<ExcelSheetWriteHandler> sheetWriteHandlerList) {
        return new ResponseExcelReturnValueHandler(sheetWriteHandlerList);
    }

}
