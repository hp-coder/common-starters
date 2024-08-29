
package com.hp.codegen.annotation.model;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenUpdateRequest {

    String packageName();

    String subPackageName() default "";

    String sourcePath() default "src/main/java";

    String classNamePrefix() default "Update";

    String classNameSuffix() default "Request";

}
