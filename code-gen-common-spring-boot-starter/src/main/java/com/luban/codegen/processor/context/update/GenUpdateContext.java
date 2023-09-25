package com.luban.codegen.processor.context.update;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenUpdateContext {
    String pkgName();
    String sourcePath() default "src/main/java";
}
