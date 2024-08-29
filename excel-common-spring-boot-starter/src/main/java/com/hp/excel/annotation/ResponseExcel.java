package com.hp.excel.annotation;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.handler.WriteHandler;
import com.hp.excel.head.HeadGenerator;
import com.hp.common.base.annotation.MethodDesc;
import com.hp.excel.enhance.ExcelWriterBuilderEnhance;

import java.lang.annotation.*;

/**
 * 导出excel
 * <p>
 * 方法需要返回对应{@code List<T>}
 * <p>
 * 其中T为数据对象
 *
 * @author hp
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseExcel {

    @MethodDesc("文件名称")
    String name() default "";

    @MethodDesc("文件类型")
    ExcelTypeEnum suffix() default ExcelTypeEnum.XLSX;

    @MethodDesc("密码")
    String password() default "";

    @MethodDesc("sheet配置")
    Sheet[] sheets() default {
            @Sheet(sheetName = "sheet1")
    };

    @MethodDesc("是否在内存中创建excel, comment或富文本只能在内存中处理")
    boolean inMemory() default false;

    @MethodDesc("通过模版导出")
    String template() default "";

    @MethodDesc("包含的列名")
    String[] include() default {};

    @MethodDesc("排除的列名")
    String[] exclude() default {};

    Class<? extends WriteHandler>[] writeHandler() default {};

    Class<? extends ExcelWriterBuilderEnhance>[] enhancement() default {};

    Class<? extends Converter>[] converter() default {};

    Class<? extends HeadGenerator> headGenerator() default HeadGenerator.class;

    boolean i18nHeader() default false;

    boolean fill() default false;
}
