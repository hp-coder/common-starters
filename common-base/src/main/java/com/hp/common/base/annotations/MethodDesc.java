
package com.hp.common.base.annotations;

import java.lang.annotation.*;

/**
 * @author HP 2023/2/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MethodDesc {
    String value();
}
