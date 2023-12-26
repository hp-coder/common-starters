package com.luban.joininmemory.annotation;

import com.luban.joininmemory.constant.ExecuteLevel;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterJoin {

    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;

}
