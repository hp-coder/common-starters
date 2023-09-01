package com.luban.joininmemory.support;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author hp
 */
@Slf4j
@Builder
public class DefaultAfterJoinMethodExecutorAdaptor<DATA_AFTER_JOIN> extends AbstractAfterJoinMethodExecutor<DATA_AFTER_JOIN> {
    private final String name;
    private final int runLevel;
    private final int runOrder;

    private final Consumer<DATA_AFTER_JOIN> afterjoin;

    public DefaultAfterJoinMethodExecutorAdaptor(
            String name,
            Integer runLevel,
            Integer runOrder,
            Consumer<DATA_AFTER_JOIN> afterjoin
    ) {
        this.name = name;
        this.afterjoin = afterjoin;
        this.runLevel = runLevel == null ? 0 : runLevel;
        this.runOrder = runOrder == null ? 0 : runOrder;
    }

    @Override
    protected void afterJoin(DATA_AFTER_JOIN data) {
        this.afterjoin.accept(data);
    }

    @Override
    public int runOnLevel() {
        return this.runLevel;
    }

    @Override
    public int runOnOrder() {
        return this.runOrder;
    }

    @Override
    public String toString() {
        return "AfterJoinExecutorAdapter-for-" + name;
    }

}
