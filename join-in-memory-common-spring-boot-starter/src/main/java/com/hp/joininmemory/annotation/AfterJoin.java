package com.hp.joininmemory.annotation;

import com.hp.joininmemory.constant.ExecuteLevel;

import java.lang.annotation.*;

/**
 *
 * 标记方法, 在join操作完成后触发
 * <p>
 * 不建议在AfterJoin中处理I/O任务. 这里最好用在CPU计算任务上'
 * <p>
 * This annotation triggers methods after join operations are completed.
 * <p>
 * Do not recommend using this annotation to process I/O tasks after join.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterJoin {

    /**
     * The execution level of methods annotated with {@code @AfterJoin}
     * <p>
     * In a nested join situation, the run level won't be recalculated as the Join processing.
     */
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;

}
