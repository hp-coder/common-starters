package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author hp 2023/3/27
 */
@Getter
@Builder
@Slf4j
public class DefaultJoinFieldExecutorAdaptor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, RESULT> extends AbstractJoinFieldExecutor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, RESULT> {

    private final String name;
    private final int runLevel;

    private final Function<SOURCE_DATA, JOIN_KEY> keyFromSource;
    private final Function<List<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader;
    private final Function<JOIN_DATA, JOIN_KEY> keyFromJoinData;
    private final Function<JOIN_DATA, RESULT> joinDataConverter;
    private final BiConsumer<SOURCE_DATA, List<RESULT>> foundCallback;
    private final BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback;


    public DefaultJoinFieldExecutorAdaptor(String name,
                                           Integer runLevel,
                                           Function<SOURCE_DATA, JOIN_KEY> keyFromSource,
                                           Function<List<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader,
                                           Function<JOIN_DATA, JOIN_KEY> keyFromJoinData,
                                           Function<JOIN_DATA, RESULT> joinDataConverter,
                                           BiConsumer<SOURCE_DATA, List<RESULT>> foundCallback,
                                           BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback) {
        Preconditions.checkArgument(keyFromSource != null);
        Preconditions.checkArgument(joinDataLoader != null);
        Preconditions.checkArgument(keyFromJoinData != null);
        Preconditions.checkArgument(joinDataConverter != null);
        Preconditions.checkArgument(foundCallback != null);
        this.name = name;
        this.keyFromSource = keyFromSource;
        this.joinDataLoader = joinDataLoader;
        this.keyFromJoinData = keyFromJoinData;
        this.joinDataConverter = joinDataConverter;
        this.foundCallback = foundCallback;
        if (lostCallback != null) {
            this.lostCallback = getDefaultLostFunction().andThen(lostCallback);
        } else {
            this.lostCallback = getDefaultLostFunction();
        }
        this.runLevel = runLevel == null ? 0 : runLevel;
    }

    private BiConsumer<SOURCE_DATA, JOIN_KEY> getDefaultLostFunction() {
        return (data, joinKey) -> log.warn("failed to find join data by {} for {}", joinKey, data);
    }

    @Override
    protected JOIN_KEY joinKeyFromSource(SOURCE_DATA sourceData) {
        return this.keyFromSource.apply(sourceData);
    }

    @Override
    protected List<JOIN_DATA> getJoinDataByJoinKeys(List<JOIN_KEY> joinKeys) {
        return this.joinDataLoader.apply(joinKeys);
    }

    @Override
    protected JOIN_KEY joinKeyFromJoinData(JOIN_DATA joinData) {
        return this.keyFromJoinData.apply(joinData);
    }

    @Override
    protected RESULT convertToResult(JOIN_DATA joinData) {
        return this.joinDataConverter.apply(joinData);
    }

    @Override
    protected void onFound(SOURCE_DATA sourceData, List<RESULT> joinResults) {
        this.foundCallback.accept(sourceData, joinResults);
    }

    @Override
    protected void onNotFound(SOURCE_DATA sourceData, JOIN_KEY joinKey) {
        this.lostCallback.accept(sourceData, joinKey);
    }

    @Override
    public int runOnLevel() {
        return this.runLevel;
    }

    @Override
    public String toString() {
        return "JoinExecutorAdapter-for-" + name;
    }
}
