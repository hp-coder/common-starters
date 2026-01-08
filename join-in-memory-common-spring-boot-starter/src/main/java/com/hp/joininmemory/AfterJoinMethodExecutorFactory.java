package com.hp.joininmemory;

import com.hp.joininmemory.context.JoinContext;

import java.util.List;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public interface AfterJoinMethodExecutorFactory {

    <DATA> List<AfterJoinMethodExecutor<DATA>> createForType(JoinContext<DATA> context);
}
