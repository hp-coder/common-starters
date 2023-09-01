package com.luban.joininmemory.annotation;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterJoin {

    /**
     * 同一个类中多个方法的执行顺序
     */
    int order() default 1;

}
