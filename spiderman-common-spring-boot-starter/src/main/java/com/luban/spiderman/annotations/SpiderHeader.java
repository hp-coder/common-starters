package com.luban.spiderman.annotations;

import com.luban.common.base.annotations.MethodDesc;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpiderHeader {

    @MethodDesc("header:key")
    String header();
    @MethodDesc("header:value")
    String value();

}
