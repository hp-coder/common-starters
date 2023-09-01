package com.luban.joininmemory.support;

import com.luban.joininmemory.AfterJoinMethodExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractAfterJoinMethodExecutor<DATA_AFTER_JOIN> implements AfterJoinMethodExecutor<DATA_AFTER_JOIN> {
    protected abstract void afterJoin(DATA_AFTER_JOIN data);

    @Override
    public void execute(DATA_AFTER_JOIN data) {
        log.debug("Executing after join method on {}", data);
        afterJoin(data);
    }
}
