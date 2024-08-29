
package com.hp.biz.logger.annotation;

import com.hp.common.base.annotation.MethodDesc;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hp
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BizDiffField {

    String alias() default "";

    @Language("SpEL")
    String value() default "";

    @MethodDesc("是否忽略")
    boolean ignored() default false;

}
