package com.luban.joininmemory.support;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author hp
 */
@Slf4j
public class DefaultAfterJoinMethodExecutorAdaptor<DATA_AFTER_JOIN> extends AbstractAfterJoinMethodExecutor<DATA_AFTER_JOIN> {

    private final String name;
    private final int runLevel;

    private final Consumer<DATA_AFTER_JOIN> afterJoin;

    public DefaultAfterJoinMethodExecutorAdaptor(
            String name,
            Integer runLevel,
            Consumer<DATA_AFTER_JOIN> afterJoin
    ) {
        this.name = name;
        this.afterJoin = afterJoin;
        this.runLevel = runLevel == null ? 0 : runLevel;
    }

    @Override
    protected void afterJoin(DATA_AFTER_JOIN data) {
        this.afterJoin.accept(data);
    }

    @Override
    public int runOnLevel() {
        return this.runLevel;
    }

    @Override
    public String toString() {
        return "AfterJoinExecutorAdapter-for-" + name;
    }

}
