package com.hp.codegen.processor.service;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenService {
    String pkgName();

    String sourcePath() default "src/main/java";
}
