package com.luban.codegen.processor.command.create;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenCreateCommand {
    String pkgName();
    String sourcePath() default "src/main/java";
}
