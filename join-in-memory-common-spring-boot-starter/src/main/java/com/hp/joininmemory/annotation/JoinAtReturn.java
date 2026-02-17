package com.hp.joininmemory.annotation;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * Annotation to mark methods whose return values require in-memory join operations.
 * <p>
 * This annotation supports Optional wrappers. If the method's return value is of type Optional, it will attempt to
 * extract the contained non-null value and perform data extraction and join operations based on the specified SpEL
 * expression.
 * <p>
 * 标记方法返回值需要进行内存连接操作的注解。
 * <p>
 * 该注解支持Optional包装器。如果方法返回值为Optional类型， 则会尝试提取其中包含的非空值，并基于指定的SpEL表达式进行数据提取和连接操作。
 *
 * @author <a href="mailto:max.p.hu@cn.pwc.com">Max.P.Hu</a>
 * @version 1.0.0
 * @since 2026/1/6
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinAtReturn {

    /**
     * SpEL表达式，用于从方法返回值中提取所需的数据集，以便后续进行连接操作。
     * <p>
     * SpEL expression used to extract the required dataset from the method's return value for subsequent join
     * operations.
     *
     * @return SpEL表达式字符串（可省略#{}格式）。 SpEL expression string (the #{} format can be omitted).
     */
    @Language("SpEL")
    String value() default "";

    /**
     * 指定在运行时连接的数据集中需要包含的字段。
     * <p>
     * Specifies the fields that should be included in the dataset during runtime join operations.
     *
     * @return 需要包含的字段数组。 Array of fields to include.
     */
    String[] included() default {};

    /**
     * 指定在运行时连接的数据集中需要排除的字段。
     * <p>
     * Specifies the fields that should be excluded from the dataset during runtime join operations.
     *
     * @return 需要排除的字段数组。 Array of fields to exclude.
     */
    String[] excluded() default {};

}

