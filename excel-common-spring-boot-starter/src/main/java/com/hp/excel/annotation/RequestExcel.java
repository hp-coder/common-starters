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

    @MethodDesc("导入功能历史设计问题, 与导出迭代后有冲突, 如果模版文件有下拉菜单, 并且是通过此框架生成, 第0个sheet用于存放下拉数据, 导入时续修改为1")
    int sheetIndex() default 0;

    @MethodDesc("是否忽略空行")
    boolean ignoreEmptyRow() default false;

}
