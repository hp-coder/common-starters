package com.hp.joininmemory;

import com.hp.joininmemory.context.JoinContext;

import java.util.List;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface AfterJoinMethodExecutorFactory {

    <DATA> List<AfterJoinMethodExecutor<DATA>> createForType(JoinContext<DATA> context);
}
