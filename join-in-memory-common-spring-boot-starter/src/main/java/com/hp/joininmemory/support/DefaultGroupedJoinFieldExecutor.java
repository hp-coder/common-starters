package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.context.JoinFieldContext;
import com.hp.joininmemory.utils.JoinHelper;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Slf4j
public class DefaultGroupedJoinFieldExecutor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

    @Getter
    protected final String name;

    @Getter
    protected final String targetClassName;

    @Getter
    protected final String targetFieldName;

    protected final List<AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> joinFieldExecutors;

    protected DefaultGroupedJoinFieldExecutor(List<AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> joinFieldExecutors) {
        Preconditions.checkArgument(CollUtil.isNotEmpty(joinFieldExecutors), "Grouped join field executors can not be empty.");
        this.joinFieldExecutors = joinFieldExecutors;

        this.targetClassName = joinFieldExecutors.getFirst().getTargetClassName();
        this.targetFieldName = joinFieldExecutors.stream().map(AbstractJoinFieldV2Executor::getTargetFieldName)
                .distinct()
                .collect(Collectors.joining(StrUtil.COMMA));

        this.name = "class[" + targetClassName + "]" +
                "#fields[" + targetFieldName + "]";
    }

    @Override
    public int runOnLevel() {
        // Based on the grouping logic, all executors in a group have the same run level.
        return joinFieldExecutors.getFirst().runOnLevel();
    }

    private List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> createJoinContext(Collection<SOURCE_DATA> sourceData) {
        return joinFieldExecutors.stream()
                .filter(executor -> JoinHelper.isIncluded(executor.getTargetClassName(), executor.getTargetFieldName()) &&
                        JoinHelper.notExcluded(executor.getTargetClassName(), executor.getTargetFieldName()))
                .map(executor -> executor.createJoinFieldContext(sourceData))
                .filter(CollUtil::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) {
            log.error("Join source data list is empty.");
            return;
        }
        doExecute(sourceDataList);
    }

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList, MeterRegistry meterRegistry) {
        // 记录数据量指标
        Gauge.builder("grouped.join.field.data.size", sourceDataList::size)
                .description("Number of data items processed by grouped field executor")
                .tags("fields", getName())
                .register(meterRegistry);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            execute(sourceDataList);
        } finally {
            sample.stop(
                    Timer.builder("grouped.join.fields.execution.time")
                            .tag("fields", getName())
                            .register(meterRegistry)
            );
        }
    }

    public void doExecute(Collection<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) {
            log.trace("The given source data is empty. Abort Join!");
            return;
        }
        final List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> contexts = createJoinContext(sourceDataList);
        if (CollUtil.isEmpty(contexts)) {
            log.trace("Join field contexts are empty. Abort Join!");
            return;
        }
        final Set<JOIN_KEY> joinKeys = contexts.stream()
                .map(JoinFieldContext::getJoinKey)
                .collect(Collectors.toSet());

        if (CollUtil.isEmpty(joinKeys)) {
            log.trace("Join keys from source are empty. Abort Join!");
            return;
        }

        final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> joinFieldV2Executor = joinFieldExecutors.getFirst();
        final Collection<JOIN_DATA> joinDataList = joinFieldV2Executor.joinDataByJoinKeys(joinKeys);
        if (CollUtil.isEmpty(joinKeys)) {
            log.trace("Join data list from datasource is empty. Abort Join!");
            return;
        }
        final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping = joinFieldV2Executor.createJoinDataMapping(joinDataList);
        if (CollUtil.isEmpty(joinDataMapping)) {
            log.trace("Grouped join: join data mapping is empty. Abort Join!");
            log.trace("Possible Reasons are: \n 1. join keys from datasource are all empty; \n 2. converted join keys from datasource are all empty. ");
            return;
        }

        final Optional<JOIN_KEY> first = joinDataMapping.keySet().stream().findFirst();
        assert first.isPresent();
        final TypeDescriptor targetType = TypeDescriptor.forObject(first.get());
        assert targetType != null;

        contexts.forEach(context -> {
            final SOURCE_DATA sourceData = context.getSourceData();
            final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor = context.getExecutor();
            final JOIN_KEY joinKey = context.getJoinKey();
            @SuppressWarnings("unchecked") final JOIN_KEY convertedJoinKey = (JOIN_KEY) STANDARD_TYPE_CONVERTER.convertValue(joinKey, TypeDescriptor.forObject(joinKey), targetType);
            final List<JOIN_DATA> mappingData = joinDataMapping.get(convertedJoinKey);
            if (CollUtil.isEmpty(mappingData)) {
                log.trace("Join data can't be found through the join key {}", joinKey);
                executor.onNotFound(sourceData, joinKey);
            } else {
                final List<JOIN_RESULT> joinResults = mappingData.stream()
                        .filter(executor::joinDataFilter)
                        .map(executor::joinDataToJoinResult)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                executor.onFound(sourceData, joinResults);
            }
        });
    }
}
