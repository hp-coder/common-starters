package com.hp.common.base.annotations;

import java.lang.annotation.*;

/**
 * @author HP 2023/2/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldDesc {
    String value();
}
