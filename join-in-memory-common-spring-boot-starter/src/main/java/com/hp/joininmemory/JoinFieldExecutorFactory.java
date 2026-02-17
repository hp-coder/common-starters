package com.hp.joininmemory;

import com.hp.joininmemory.context.JoinContext;

import java.util.List;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface JoinFieldExecutorFactory {

    /**
     * 构建对应属性的数据装载执行器
     *
     * @param joinContext 上下文 {@link  JoinContext}, 方便传递数据至下游
     * @param <DATA>      属性 值
     * @return 一个数据装载处理器的集合
     */
    <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> joinContext);
}
