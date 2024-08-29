package com.hp.codegen.annotation.infrastructure;

import com.hp.codegen.constant.GenerateTarget;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenConverter {
    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "";

    String classNameSuffix() default "Converters";

    GenerateTarget target() default GenerateTarget.SOURCE;
}
