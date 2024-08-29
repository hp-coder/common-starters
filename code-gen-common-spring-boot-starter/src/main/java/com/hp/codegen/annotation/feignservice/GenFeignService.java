package com.hp.codegen.annotation.feignservice;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenCommandFeignService(packageName = "")
@GenQueryFeignService(packageName = "")
public @interface GenFeignService {

    String packageName();

    String sourcePath() default "src/main/java";
}
