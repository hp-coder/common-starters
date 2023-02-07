package com.luban.excel.annotation;

import com.luban.excel.listener.DefaultExcelAnalysisEventListener;
import com.luban.excel.listener.ExcelAnalysisEventListener;
import com.luban.excel.enhence.ExcelReaderBuilderEnhance;

import java.lang.annotation.*;

/**
 * 导入excel*
 * <p>
 * API方法入参需要指定List<T> 或带合并行数据 Map<RowIndex,List<T>>*
 * 其中T类型为数据对象*
 *
 * @author HP
 * @date 2022/11/7
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestExcel {

    /**
     * 表单提交时文件参数的名称*
     *
     * @return
     */
    String filename() default "file";

    /**
     * 基于EasyExcel的自定义数据处理监听器*
     *
     * @return
     */
    Class<? extends ExcelAnalysisEventListener<?, ?>> listener() default DefaultExcelAnalysisEventListener.class;

    Class<? extends ExcelReaderBuilderEnhance>[] enhancement() default {};

    /**
     * 是否忽略空行*
     *
     * @return
     */
    boolean ignoreEmptyRow() default false;
}
