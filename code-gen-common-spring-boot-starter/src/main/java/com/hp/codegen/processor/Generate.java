package com.hp.codegen.processor;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Generate {
    String pkgName();
    String sourcePath() default "src/main/java";
}
