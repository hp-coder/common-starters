package com.hp.codegen.processor.repository;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenRepository {
    String pkgName();

    String sourcePath() default "src/main/java";
}
