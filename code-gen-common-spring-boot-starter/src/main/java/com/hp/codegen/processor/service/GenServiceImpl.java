package com.hp.codegen.processor.service;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenServiceImpl {
    String pkgName();
    String sourcePath() default "src/main/java";
    boolean overrideSource() default false;
}
