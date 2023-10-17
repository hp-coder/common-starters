package com.luban.joininmemory.annotation;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterJoin {

    int runLevel() default 10;

}
