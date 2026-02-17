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
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
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

    protected final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> first;

    protected DefaultGroupedJoinFieldExecutor(List<AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> joinFieldExecutors) {
        Preconditions.checkArgument(CollUtil.isNotEmpty(joinFieldExecutors), "Grouped join field executors can not be empty.");
        this.joinFieldExecutors = joinFieldExecutors;
        // 同组的执行器逻辑基本是一致的. 默认拿第一个, 用来处理公共逻辑
        this.first = joinFieldExecutors.get(0);

        this.targetClassName = first.getTargetClassName();
        this.targetFieldName = joinFieldExecutors.stream()
                .map(AbstractJoinFieldV2Executor::getTargetFieldName)
                .distinct()
                .collect(Collectors.joining(StrUtil.COMMA));

        this.name = "class[" + targetClassName + "]" +
                "#fields[" + targetFieldName + "]";
    }

    @Override
    public int runOnLevel() {
        // Based on the grouping logic, all executors in a group have the same run level.
        return first.runOnLevel();
    }

    /**
     * Grouped场景的主要差异在于给源数据属性赋值的时候, 各个处理器的源属性不同而已
     */
    private List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> createJoinContext(Collection<SOURCE_DATA> sourceData) {
        return joinFieldExecutors.stream()
                .filter(executor -> JoinHelper.isValidField(executor.getTargetClassName(), executor.getTargetFieldName()))
                .map(executor -> executor.createJoinFieldContext(sourceData))
                .filter(CollUtil::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) {
            log.debug("GroupJoinExecutor-[{}] sourceData is empty", getName());
            return;
        }
        // 针对 nested join 提供支持 since 1.1.0
        final List<SOURCE_DATA> list = sourceDataList.stream().flatMap(data -> first.extractSouceData(data).stream()).toList();
        // 这里的数据实际时根据嵌套层级展开后的数据
        doExecute(list);
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
        final List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> contexts = createJoinContext(sourceDataList);
        if (CollUtil.isEmpty(contexts)) {
            log.debug("GroupJoinExecutor-[{}] JoinFieldContext is empty", getName());
            return;
        }
        // 组里所有的JoinKey
        final Set<JOIN_KEY> joinKeys = contexts.stream()
                .map(JoinFieldContext::getJoinKey)
                .collect(Collectors.toSet());

        if (CollUtil.isEmpty(joinKeys)) {
            log.debug("GroupJoinExecutor-[{}] JoinKey is empty", getName());
            return;
        }

        // 查询逻辑是一样的, 直接用第一个处理器的逻辑获取数据
        final Collection<JOIN_DATA> joinDataList = first.joinDataByJoinKeys(joinKeys);
        if (CollUtil.isEmpty(joinKeys)) {
            log.debug("GroupJoinExecutor-[{}] JoinData is empty", getName());
            return;
        }

        // 构造映射逻辑也是一样的
        final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping = first.createJoinDataMapping(joinDataList);
        if (CollUtil.isEmpty(joinDataMapping)) {
            log.debug("GroupJoinExecutor-[{}] JoinDataMapping is empty", getName());
            log.trace("Possible Reasons are: \n 1. join keys from datasource are all empty; \n 2. converted join keys from datasource are all empty. ");
            return;
        }

        // 将两侧的关联键类型统一
        final Optional<JOIN_KEY> firstJoinKey = joinDataMapping.keySet().stream().findFirst();
        assert firstJoinKey.isPresent();
        final TypeDescriptor targetType = TypeDescriptor.forObject(firstJoinKey.get());
        assert targetType != null;

        contexts.forEach(context -> {
            final SOURCE_DATA sourceData = context.getSourceData();
            final JOIN_KEY joinKey = context.getJoinKey();

            // 每个源join属性的处理器单独处理赋值操作
            final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor = context.getExecutor();

            // 同类型的关联键
            @SuppressWarnings("unchecked") final JOIN_KEY convertedJoinKey = (JOIN_KEY) STANDARD_TYPE_CONVERTER.convertValue(joinKey, TypeDescriptor.forObject(joinKey), targetType);

            // 获取关联数据
            final List<JOIN_DATA> mappingData = joinDataMapping.get(convertedJoinKey);

            if (CollUtil.isEmpty(mappingData)) {
                log.debug("GroupJoinExecutor-[{}] JoinData NotFound; JoinKey-[{}] ConvertedJoinKey-[{}]", getName(), joinKey, convertedJoinKey);
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

    @Override
    public String groupingKey() {
        return first.groupingKey();
    }
}
