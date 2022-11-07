package com.hp.excel.annotation;

import com.hp.excel.listener.DefaultExcelAnalysisEventListener;
import com.hp.excel.listener.ExcelAnalysisEventListener;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/11/7
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestExcel {

    /**
     * 表单提交时文件参数的名称*
     * @return
     */
    String filename() default "file";

    /**
     * 基于EasyExcel的自定义数据处理监听器*
     * @return
     */
    Class<? extends ExcelAnalysisEventListener<?>> analysisEventListener() default DefaultExcelAnalysisEventListener.class;

    /**
     * 是否忽略空行*
     * @return
     */
    boolean ignoreEmptyRow() default false;
}
