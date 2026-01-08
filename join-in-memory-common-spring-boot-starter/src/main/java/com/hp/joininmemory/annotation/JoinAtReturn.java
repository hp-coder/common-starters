package com.hp.joininmemory.annotation;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * Optional Wrapper supported.
 * <p>
 * 如果返回值为Optional, 则尝试提取其包含的非空值进行表达式解析并执行Join操作
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinAtReturn {

    /**
     * The SpEL expression extracts the desired data set from
     * the method return value for the later join process.
     *
     * @return SpEL expression. (#{} format can be left out)
     */
    @Language("SpEL")
    String value() default "";
}

