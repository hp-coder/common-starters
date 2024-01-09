package com.hp.joininmemory;

import com.hp.joininmemory.context.JoinContext;

import java.util.List;

/**
 * @author hp
 */
public interface AfterJoinMethodExecutorFactory {

    <DATA> List<AfterJoinMethodExecutor<DATA>> createForType(JoinContext<DATA> context);
}
