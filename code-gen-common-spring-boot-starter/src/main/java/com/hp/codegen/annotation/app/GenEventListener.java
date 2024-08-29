package com.hp.codegen.annotation.app;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenEventListener {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "";

    String classNameSuffix() default "EventHandler";
}
