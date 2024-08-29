package com.hp.codegen.annotation.domain;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenUpdateContext {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "Update";

    String classNameSuffix() default "Context";
}
