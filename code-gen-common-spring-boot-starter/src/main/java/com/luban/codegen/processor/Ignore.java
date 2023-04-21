package com.luban.codegen.processor;

import java.lang.annotation.*;

/**
 * @author HP
 * @date 2022/10/24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {
}
