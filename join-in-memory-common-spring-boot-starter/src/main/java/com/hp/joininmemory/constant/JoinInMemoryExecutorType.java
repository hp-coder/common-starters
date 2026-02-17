package com.hp.joininmemory.constant;


/**
 * For {@link JoinInMemoryExecutorType#PARALLEL}, the framework will use a configured
 * thread pool to execute join tasks, and tasks that have the same execution level will be submitted at once.
 * <p>
 * For {@link JoinInMemoryExecutorType#SERIAL}, each join field will be submitted one
 * by one according to the tasks' run level, which means the execution will take more time to finish.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public enum JoinInMemoryExecutorType {
    PARALLEL,
    SERIAL
}
