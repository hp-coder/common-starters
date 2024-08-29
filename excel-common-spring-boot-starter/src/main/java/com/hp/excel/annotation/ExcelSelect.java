package com.hp.excel.annotation;

import com.hp.excel.validation.ExcelSelectConstraintValidator;
import com.hp.common.base.annotation.MethodDesc;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Constraint(validatedBy = ExcelSelectConstraintValidator.class)
public @interface ExcelSelect {

    @MethodDesc("下拉项")
    ExcelOptions options();

    @MethodDesc("父列列名")
    String parentColumnName() default "";

    @MethodDesc("设置下拉的起始行，默认为第二行")
    int firstRow() default 1;

    @MethodDesc("设置下拉的结束行")
    int lastRow() default 0x10000;

    @MethodDesc("校验相关,提示信息")
    String message() default "请填写规定范围的值";

    @MethodDesc("校验相关,分组")
    Class<?>[] groups() default {};

    @MethodDesc("校验相关,元数据")
    Class<? extends Payload>[] payload() default {};
}
