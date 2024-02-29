package com.hp.codegen.processor.context.create;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenCreateContext {
    String pkgName();
    String sourcePath() default "src/main/java";
}
