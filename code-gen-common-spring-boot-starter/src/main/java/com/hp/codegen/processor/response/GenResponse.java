package com.hp.codegen.processor.response;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenResponse {
    String pkgName();

    String sourcePath() default "src/main/java";

}
