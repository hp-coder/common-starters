package com.hp.excel.annotation;


import com.hp.excel.handler.ColumnDynamicSelectDataHandler;
import com.hp.excel.handler.DefaultColumnDynamicSelectDataHandler;
import com.hp.excel.valid.DynamicSelectDataValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Constraint(validatedBy = DynamicSelectDataValidator.class)  // 校验器
public @interface DynamicSelectData {

    String message() default "请填写规定范围的值";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String parameter() default "";

    Class<? extends ColumnDynamicSelectDataHandler> handler() default DefaultColumnDynamicSelectDataHandler.class;
}
