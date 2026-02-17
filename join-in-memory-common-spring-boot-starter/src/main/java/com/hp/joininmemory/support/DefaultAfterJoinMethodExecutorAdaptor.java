package com.hp.joininmemory.support;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public class DefaultAfterJoinMethodExecutorAdaptor<DATA> extends AbstractAfterJoinMethodExecutor<DATA> {

    private final String name;
    private final int runLevel;
    private final List<String> hierarchicalPaths;

    private final BiFunction<DATA, List<String>, Collection<DATA>> extractSourceData;
    private final Consumer<DATA> afterJoin;

    public DefaultAfterJoinMethodExecutorAdaptor(
            String name,
            Integer runLevel,
            List<String> hierarchicalPaths,
            BiFunction<DATA, List<String>, Collection<DATA>> extractSourceData,
            Consumer<DATA> afterJoin
    ) {
        this.name = name;
        this.hierarchicalPaths = hierarchicalPaths;
        this.extractSourceData = extractSourceData;
        this.afterJoin = afterJoin;
        this.runLevel = runLevel == null ? 0 : runLevel;
    }

    @Override
    protected void afterJoin(DATA data) {
        this.afterJoin.accept(data);
    }

    @Override
    protected Collection<DATA> extractSourceData(DATA data) {
        log.trace("hierarchicalPaths: {}", hierarchicalPaths);
        final Collection<DATA> extractedData = extractSourceData.apply(data, hierarchicalPaths);
        log.trace("extractedData: {}", extractedData);
        return extractedData;
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
