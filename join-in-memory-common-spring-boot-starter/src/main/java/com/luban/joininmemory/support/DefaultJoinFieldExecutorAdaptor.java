package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author hp 2023/3/27
 */
@Getter
@Slf4j
public class DefaultJoinFieldExecutorAdaptor<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> extends AbstractJoinFieldV2Executor<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> {

    private final String name;
    private final int runLevel;

    private final Function<SOURCE_DATA, SOURCE_JOIN_KEY> keyFromSource;
    private final Function<SOURCE_JOIN_KEY, JOIN_KEY> convertKeyFromSourceData;
    private final Function<Collection<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader;
    private final Function<JOIN_DATA, DATA_JOIN_KEY> keyFromJoinData;
    private final Function<DATA_JOIN_KEY, JOIN_KEY> convertKeyFromJoinData;
    private final Function<JOIN_DATA, JOIN_RESULT> joinDataConverter;
    private final BiConsumer<SOURCE_DATA, List<JOIN_RESULT>> foundCallback;
    private final BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback;


    public DefaultJoinFieldExecutorAdaptor(String name,
                                           int runLevel,
                                           Function<SOURCE_DATA, SOURCE_JOIN_KEY> keyFromSource,
                                           Function<SOURCE_JOIN_KEY, JOIN_KEY> convertKeyFromSourceData,
                                           Function<Collection<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader,
                                           Function<JOIN_DATA, DATA_JOIN_KEY> keyFromJoinData,
                                           Function<DATA_JOIN_KEY, JOIN_KEY> convertKeyFromJoinData,
                                           Function<JOIN_DATA, JOIN_RESULT> joinDataConverter,
                                           BiConsumer<SOURCE_DATA, List<JOIN_RESULT>> foundCallback,
                                           BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback) {
        Preconditions.checkArgument(keyFromSource != null);
        Preconditions.checkArgument(joinDataLoader != null);
        Preconditions.checkArgument(keyFromJoinData != null);
        Preconditions.checkArgument(joinDataConverter != null);
        Preconditions.checkArgument(foundCallback != null);
        this.name = name;
        this.keyFromSource = keyFromSource;
        this.convertKeyFromSourceData = convertKeyFromSourceData;
        this.joinDataLoader = joinDataLoader;
        this.keyFromJoinData = keyFromJoinData;
        this.convertKeyFromJoinData = convertKeyFromJoinData;
        this.joinDataConverter = joinDataConverter;
        this.foundCallback = foundCallback;
        if (lostCallback != null) {
            this.lostCallback = getDefaultLostFunction().andThen(lostCallback);
        } else {
            this.lostCallback = getDefaultLostFunction();
        }
        this.runLevel = runLevel;
    }

    private BiConsumer<SOURCE_DATA, JOIN_KEY> getDefaultLostFunction() {
        return (data, joinKey) -> log.debug("failed to find join data by {} for {}", joinKey, data);
    }

    @Override
    protected SOURCE_JOIN_KEY joinKeyFromSource(SOURCE_DATA sourceData) {
        return this.keyFromSource.apply(sourceData);
    }

    @Override
    protected JOIN_KEY sourceJoinKeyToJoinKey(SOURCE_JOIN_KEY joinKey) {
        return this.convertKeyFromSourceData.apply(joinKey);
    }

    @Override
    protected List<JOIN_DATA> joinDataByJoinKeys(Collection<JOIN_KEY> joinKeys) {
        return this.joinDataLoader.apply(joinKeys);
    }

    @Override
    protected DATA_JOIN_KEY dataJoinKeyFromJoinData(JOIN_DATA joinData) {
        return this.keyFromJoinData.apply(joinData);
    }

    @Override
    protected JOIN_KEY dataJoinKeyToJoinKey(DATA_JOIN_KEY joinKey) {
        return this.convertKeyFromJoinData.apply(joinKey);
    }

    @Override
    protected JOIN_RESULT joinDataToJoinResult(JOIN_DATA joinData) {
        return this.joinDataConverter.apply(joinData);
    }

    @Override
    protected void onFound(SOURCE_DATA sourceData, List<JOIN_RESULT> joinResults) {
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
