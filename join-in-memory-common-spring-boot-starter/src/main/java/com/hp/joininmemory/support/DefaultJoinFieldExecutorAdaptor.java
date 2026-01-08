package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hp.joininmemory.utils.JoinHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Getter
@Slf4j
public class DefaultJoinFieldExecutorAdaptor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> extends AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> {

    private static final Pattern nameExtractor = Pattern.compile("class\\[([^]]+)]#field\\[([^]]+)]");

    private final String name;
    private final int batchSize;
    private final int runLevel;

    private final Function<SOURCE_DATA, Boolean> sourceDataFilter;
    private final Function<SOURCE_DATA, JOIN_KEY> keyFromSource;
    private final Function<Collection<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader;
    private final Function<JOIN_DATA, JOIN_KEY> keyFromJoinData;
    private final Function<JOIN_DATA, Boolean> joinDataFilter;
    private final Function<JOIN_DATA, JOIN_RESULT> joinDataConverter;
    private final BiConsumer<SOURCE_DATA, Collection<JOIN_RESULT>> foundCallback;
    private final BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback;

    public DefaultJoinFieldExecutorAdaptor(
            String name,
            int runLevel,
            Function<SOURCE_DATA, Boolean> sourceDataFilter,
            Function<SOURCE_DATA, JOIN_KEY> keyFromSource,
            Function<Collection<JOIN_KEY>, List<JOIN_DATA>> joinDataLoader,
            Function<JOIN_DATA, JOIN_KEY> keyFromJoinData,
            Function<JOIN_DATA, Boolean> joinDataFilter,
            Function<JOIN_DATA, JOIN_RESULT> joinDataConverter,
            BiConsumer<SOURCE_DATA, Collection<JOIN_RESULT>> foundCallback,
            BiConsumer<SOURCE_DATA, JOIN_KEY> lostCallback
    ) {
        this.name = name;
        this.sourceDataFilter = sourceDataFilter;
        this.keyFromSource = Objects.requireNonNull(keyFromSource);
        this.joinDataLoader = Objects.requireNonNull(joinDataLoader);
        this.keyFromJoinData = Objects.requireNonNull(keyFromJoinData);
        this.joinDataFilter = joinDataFilter;
        this.joinDataConverter = joinDataConverter;
        this.foundCallback = Objects.requireNonNull(foundCallback);
        if (lostCallback != null) {
            this.lostCallback = getDefaultLostFunction().andThen(lostCallback);
        } else {
            this.lostCallback = getDefaultLostFunction();
        }
        this.runLevel = runLevel;

        this.batchSize = JoinHelper.getJoinKeyBatch();
    }

    private BiConsumer<SOURCE_DATA, JOIN_KEY> getDefaultLostFunction() {
        return (data, joinKey) -> log.debug("failed to find join data by {} for {}", joinKey, data);
    }

    @Override
    protected boolean sourceDataFilter(SOURCE_DATA data) {
        return this.sourceDataFilter.apply(data);
    }

    @Override
    protected JOIN_KEY joinKeyFromSourceData(SOURCE_DATA sourceData) {
        return this.keyFromSource.apply(sourceData);
    }

    @Override
    protected Collection<JOIN_DATA> joinDataByJoinKeys(Collection<JOIN_KEY> joinKeys) {
        if (CollUtil.isEmpty(joinKeys)) {
            return Collections.emptyList();
        }
        final List<List<JOIN_KEY>> batchedJoinKeys = JoinHelper.batchJoinKeys(joinKeys, batchSize);

        return batchedJoinKeys.stream()
                .flatMap(keys -> this.joinDataLoader.apply(keys).stream())
                .toList();
    }

    @Override
    protected JOIN_KEY joinKeyFromJoinData(JOIN_DATA joinData) {
        return this.keyFromJoinData.apply(joinData);
    }

    @Override
    protected boolean joinDataFilter(JOIN_DATA joinData) {
        return this.joinDataFilter.apply(joinData);
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
    public String getTargetClassName() {
        final Matcher matcher = nameExtractor.matcher(name);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return StrUtil.EMPTY;
    }

    @Override
    public String getTargetFieldName() {
        final Matcher matcher = nameExtractor.matcher(name);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return StrUtil.EMPTY;
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
