package com.luban.excel.annotation;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.handler.WriteHandler;
import com.luban.excel.enhence.ExcelWriterBuilderEnhance;
import com.luban.excel.head.HeadGenerator;

import java.lang.annotation.*;

/**
 * 导出excel
 * <p>
 * 方法需要返回对应List<T>
 *
 * @author hp
 * @date 2022/11/7
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseExcel {
    String name() default "";

    /**
     * 文件后缀*
     *
     * @return
     */
    ExcelTypeEnum suffix() default ExcelTypeEnum.XLSX;

    /**
     * 是否添加密码*
     *
     * @return
     */
    String password() default "";

    /**
     * sheet*
     *
     * @return
     */
    Sheet[] sheets() default {@Sheet(
            sheetName = "sheet1"
    )};

    boolean inMemory() default false;

    String template() default "";

    String[] include() default {};

    String[] exclude() default {};

    Class<? extends WriteHandler>[] writeHandler() default {};

    Class<? extends ExcelWriterBuilderEnhance>[] enhancement() default {};

    Class<? extends Converter>[] converter() default {};

    Class<? extends HeadGenerator> headGenerator() default HeadGenerator.class;

    boolean i18nHeader() default false;

    boolean fill() default false;
}
