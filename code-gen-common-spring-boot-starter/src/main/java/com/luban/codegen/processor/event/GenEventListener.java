package com.luban.codegen.processor.event;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenEventListener {
    String pkgName();
    String sourcePath() default "src/main/java";
}
