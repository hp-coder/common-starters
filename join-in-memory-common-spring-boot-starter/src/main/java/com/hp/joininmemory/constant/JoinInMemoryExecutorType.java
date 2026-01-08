package com.hp.joininmemory.constant;


/**
 * For {@link JoinInMemoryExecutorType#PARALLEL}, the framework will create a thread pool to execute the join task, and
 * tasks have the same execution level will be submitted at the same time.
 * <p>
 * For {@link JoinInMemoryExecutorType#SERIAL}, each join field will be submitted separately, which means the execution
 * will take more time to finish.
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public enum JoinInMemoryExecutorType {
    PARALLEL,
    SERIAL
}
