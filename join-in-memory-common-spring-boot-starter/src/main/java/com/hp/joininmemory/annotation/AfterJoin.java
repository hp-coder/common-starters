package com.hp.joininmemory.annotation;


import com.hp.joininmemory.constant.ExecuteLevel;

import java.lang.annotation.*;

/**
 * This annotation triggers methods after join operations are completed.
 * <p>
 * Do not recommend using this annotation to process I/O tasks after join.
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterJoin {

    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;

}
