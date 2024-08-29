package com.hp.codegen.annotation.app;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenCommandAppService(packageName = "")
@GenCommandAppServiceImpl(packageName = "", subPackageName = "impl")
@GenEventListener(packageName = "", subPackageName = "event")
@GenQueryAppService(packageName = "")
@GenQueryAppServiceImpl(packageName = "", subPackageName = "impl")
public @interface GenApp {
    String packageName();

    String sourcePath() default "src/main/java";
}
