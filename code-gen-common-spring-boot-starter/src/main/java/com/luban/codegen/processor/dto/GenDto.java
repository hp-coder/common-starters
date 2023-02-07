package com.luban.codegen.processor.dto;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenDto {
    String pkgName();

    String sourcePath() default "src/main/java";
}
