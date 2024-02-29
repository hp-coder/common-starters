package com.hp.codegen.processor.event;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenEvent {
    String pkgName();
    String sourcePath() default "src/main/java";
}
