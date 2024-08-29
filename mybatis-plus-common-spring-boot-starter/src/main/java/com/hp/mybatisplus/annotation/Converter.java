package com.hp.mybatisplus.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author hp
 */
@Inherited
@Target({TYPE})
@Retention(RUNTIME)
public @interface Converter {
}
