package com.hp.codegen.annotation.model;

import com.hp.codegen.constant.GenerateTarget;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenSerializer {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "";

    String classNameSuffix() default "Serializers";

    GenerateTarget target() default GenerateTarget.SOURCE;
}
