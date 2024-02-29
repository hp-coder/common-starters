package com.luban.joininmemory.annotation;

import java.lang.annotation.*;

/**
 * @since 1.0.1-sp3.2-SNAPSHOT
 * @author hp
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
    String value() default "";
}

