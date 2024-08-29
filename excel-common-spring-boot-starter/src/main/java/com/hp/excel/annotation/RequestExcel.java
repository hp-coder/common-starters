package com.hp.excel.annotation;

import com.hp.excel.listener.DefaultExcelAnalysisEventListener;
import com.hp.excel.listener.ExcelAnalysisEventListener;
import com.hp.common.base.annotation.MethodDesc;
import com.hp.excel.enhance.ExcelReaderBuilderEnhance;

import java.lang.annotation.*;

/**
 * 导入excel
 * <p>
 * API方法入参需要指定{@code List<T>} 或 带合并行数据 {@code Map<RowIndex,List<T>>}
 * <p>
 * 其中T为数据对象
 *
 * @author hp
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestExcel {

    @MethodDesc("表单提交时文件参数的名称")
    String filename() default "file";

    @MethodDesc("基于EasyExcel的自定义数据处理监听器")
    Class<? extends ExcelAnalysisEventListener<?, ?>> listener() default DefaultExcelAnalysisEventListener.class;

    @MethodDesc("对导入的增强")
    Class<? extends ExcelReaderBuilderEnhance>[] enhancement() default {};

    @MethodDesc("是否忽略空行")
    boolean ignoreEmptyRow() default false;
}
