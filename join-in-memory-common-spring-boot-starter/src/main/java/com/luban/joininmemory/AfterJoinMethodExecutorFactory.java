package com.luban.joininmemory;

import java.util.List;

/**
 * @author hp
 */
public interface AfterJoinMethodExecutorFactory {

    <TYPE> List<AfterJoinMethodExecutor<TYPE>> createForType(Class<TYPE> clazz);
}
