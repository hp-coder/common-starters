package com.hp.codegen.annotation.api;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenCommandApi {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String apiVersion() default "v1";

    String classNamePrefix() default "";

    String classNameSuffix() default "CommandApi";
}
