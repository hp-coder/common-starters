package com.hp.codegen.annotation.api;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenCommandFeignClient {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String serverName();

    String classNamePrefix() default "";

    String classNameSuffix() default "CommandFeignClient";
}
