package com.hp.excel.annotation;


import com.hp.excel.head.HeadGenerator;

import java.lang.annotation.*;

/**
 * sheet名称*
 * @author HP
 * @date 2022/11/7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheet {
    int sheetNo() default -1;

    String sheetName();

    String[] includes() default {};

    String[] excludes() default {};

    Class<? extends HeadGenerator> headGenerateClass() default HeadGenerator.class;
}
