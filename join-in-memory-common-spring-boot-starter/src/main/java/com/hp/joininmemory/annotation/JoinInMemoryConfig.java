package com.hp.joininmemory.annotation;

import com.hp.joininmemory.JoinInMemoryAutoConfiguration;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;

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

    /**
     * Defines how to execute tasks.
     * <p>
     * Note:
     * <p>
     * Using the default thread pool,
     * which was using a SynchronousQueue to store tasks,
     * will fail at nested join tasks.
     * When defining nested join is unavoidable, use SERIAL.
     * This specific scenario usually occurs when @AfterJoin is used.
     * <p>
     * Due to executing tasks with different thread pools,
     * mix-using both executor types is unsafe and will throw a runtime exception.
     *
     * @return way to execute tasks
     * @see AfterJoin
     * @see JoinInMemoryAutoConfiguration
     */
    JoinInMemoryExecutorType executorType() default JoinInMemoryExecutorType.SERIAL;

    /**
     * ExecutorService used to execute tasks
     * <p>
     * Note:
     * <p>
     * Make sure the class is already correctly registered as a Spring Bean.
     *
     * @return the bean name of the defined executor service.
     */
    String executorName() default "defaultJoinInMemoryExecutor";

    /**
     * This value defines whether to combine multiple fields
     * that use the same join-in-memory annotation in a class
     * during join-processing.
     * <p>
     * Note:
     * <p>
     * Since fields defined in the {@code @JoinInMemory} can be
     * overridden freely, {@code JoinFieldProcessPolicy.GROUPED}
     * will only combine those fields annotated with the same
     * join-annotation which has the same {@code JoinInMemory.runLevel()}
     * value and the same {@code JoinInMemory.loader()} value.
     * <p>
     * By default, GROUPED.
     */
    JoinFieldProcessPolicy fieldProcessPolicy() default JoinFieldProcessPolicy.GROUPED;

}
