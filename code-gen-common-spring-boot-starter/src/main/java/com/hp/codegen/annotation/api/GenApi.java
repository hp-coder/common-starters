package com.hp.codegen.annotation.api;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenCommandApi(packageName = "")
@GenCommandFeignClient(packageName = "", serverName = "", subPackageName = "client")
@GenQueryApi(packageName = "")
@GenQueryFeignClient(packageName = "", serverName = "", subPackageName = "client")
public @interface GenApi {

    String packageName();

    String sourcePath() default "src/main/java";

    String serverName();

    String apiVersion() default "v1";
}
