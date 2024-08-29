

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
public @interface BizDiffer {

    @MethodDesc("未修改的对象, 使用#+变量名称格式, 如, #order")
    @Language("SpEL")
    String before() default "";

    @MethodDesc("修改后的对象, 使用#+变量名称格式, 一般这个修改后的值需要通过BizLoggerContext手动设置变量, 如, #changedOrder")
    @Language("SpEL")
    String after() default "";

    @MethodDesc("是否忽略")
    boolean ignored() default false;

}
