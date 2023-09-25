package com.luban.codegen.processor.command.update;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenUpdateCommand {
    String pkgName();
    String sourcePath() default "src/main/java";
}
