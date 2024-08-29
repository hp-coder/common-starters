package com.hp.codegen.annotation.infrastructure;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenCommandRepositoryAdapter {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "";

    String classNameSuffix() default "CommandRepositoryAdapter";
}
