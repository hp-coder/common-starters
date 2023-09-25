
package com.luban.codegen.processor.request;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenUpdateRequest {
    String pkgName();
    String sourcePath() default "src/main/java";
}
