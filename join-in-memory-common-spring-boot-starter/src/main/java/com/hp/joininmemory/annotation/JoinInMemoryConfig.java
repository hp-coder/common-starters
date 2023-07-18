package com.hp.joininmemory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemoryConfig {
    JoinInMemoryExecutorType executorType() default JoinInMemoryExecutorType.SERIAL;

    String executorName() default "defaultJoinInMemoryExecutor";
}
