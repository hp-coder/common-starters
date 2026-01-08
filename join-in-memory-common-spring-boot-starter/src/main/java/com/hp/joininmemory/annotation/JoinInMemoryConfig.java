package com.hp.joininmemory.annotation;

import com.hp.joininmemory.JoinInMemoryAutoConfiguration;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import com.hp.joininmemory.support.JoinInMemoryBasedJoinFieldExecutorFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;


/**
 * This config annotation is used to configure join policies for each class.
 * <p>
 * If the join class was not annotated with {@code @JoinInMemoryConfig}, a default config will be used.
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemoryConfig {

    /**
     * Defines how to execute tasks.
     * <p>
     * Note:
     * <p>
     * Use the default thread pool, which uses a SynchronousQueue to store tasks, will fail at nested join tasks. When
     * defining nested join is unavoidable, use SERIAL. This specific scenario usually occurs when @AfterJoin is used.
     * <p>
     * Due to executing tasks with different thread pools, mixing both executor types is unsafe and will throw a runtime
     * exception.
     * <p>
     * By default, PARALLEL
     *
     * @return way to execute tasks
     * @see AfterJoin
     * @see JoinInMemoryAutoConfiguration
     */
    JoinInMemoryExecutorType executorType() default JoinInMemoryExecutorType.PARALLEL;

    /**
     * ExecutorService used to execute join tasks
     * <p>
     * Note:
     * <p>
     * Make sure the instance is already correctly registered as a Spring Bean.
     * <p>
     * The default join executor service is {@link JoinInMemoryAutoConfiguration#defaultJoinInMemoryExecutor()}.
     *
     * @return the bean name of the defined executor service.
     */
    String executorName() default "defaultJoinInMemoryExecutor";

    /**
     * This value defines whether to combine multiple fields that use the same join annotation within a class during
     * join-processing.
     * <p>
     * Note:
     * <p>
     * Since fields defined in the {@code @JoinInMemory} can be overridden freely,
     * {@code JoinFieldProcessPolicy.GROUPED} will only combine those fields annotated with the same join-annotation
     * which has the same value.
     * <p>
     * Attributes used to determine groups:
     * {@link JoinInMemoryBasedJoinFieldExecutorFactory#groupBy(Class, Field, JoinInMemory)}
     *
     * <p>
     * By default, GROUPED.
     */
    JoinFieldProcessPolicy fieldProcessPolicy() default JoinFieldProcessPolicy.GROUPED;

    /**
     * The batch size of join tasks.
     * <p>
     * Ignore when 0 or negative value is provided.
     * <p>
     * For example, if a collection of 5000 elements is provided to trigger a join service, for query optimization, the
     * collection will be divided into 5 batches, each batch contains 1000 elements.
     * <p>
     * experimental feature
     */
    int joinBatchSize() default 1000;

}
