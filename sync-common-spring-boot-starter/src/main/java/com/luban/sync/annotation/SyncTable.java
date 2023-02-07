package com.hp.sync.annotation;

import java.lang.annotation.*;

/**
 * @author HP 2022/12/7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SyncTable {

    String value();
}
