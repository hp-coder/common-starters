package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.context.JoinFieldContext;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public abstract class AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

    /**
     * 从源数据中提取处理数据
     * <p>
     * 针对一般场景, 基本没有处理, 最多包装为Collection即可
     * <p>
     * 针对嵌套场景, 需要根据嵌套层级逐层展开, 提取到join操作对应需要的数据源
     */
    protected abstract Collection<SOURCE_DATA> extractSouceData(SOURCE_DATA rawData);

    /**
     * 过滤数据
     *
     * @param data 原始数据对象
     * @return 如果被过滤, 则应返回false
     */
    protected abstract boolean sourceDataFilter(SOURCE_DATA data);

    /**
     * 从原始数据中生成 JoinKey
     *
     * @param data 原始数据对象
     * @return 关联属性值
     */
    protected abstract JOIN_KEY joinKeyFromSourceData(SOURCE_DATA data);

    /**
     * 根据 JoinKey 批量获取 JoinData
     *
     * @param joinKeys 关联属性值
     * @return 关联数据
     */
    protected abstract Collection<JOIN_DATA> joinDataByJoinKeys(Collection<JOIN_KEY> joinKeys);

    /**
     * 从 JoinData 中获取 JoinKey
     *
     * @param joinData 关联属性数据
     * @return 关联属性数据形成 Map 时的 key
     */
    protected abstract JOIN_KEY joinKeyFromJoinData(JOIN_DATA joinData);

    /**
     * 过滤数据
     *
     * @param data 查询出的关联数据
     * @return 如果被过滤, 则应返回false
     */
    protected abstract boolean joinDataFilter(JOIN_DATA data);

    /**
     * 将 JoinData 转换为 JoinResult
     *
     * @param joinData 关联数据
     * @return 转换后的数据结果，如：entity -> VO
     */
    protected abstract JOIN_RESULT joinDataToJoinResult(JOIN_DATA joinData);

    /**
     * 将 JoinResult 写回至 SourceData，定义如何写回数据对象
     *
     * @param data        源数据
     * @param joinResults 转换后的关联数据集
     */
    protected abstract void onFound(SOURCE_DATA data, List<JOIN_RESULT> joinResults);

    /**
     * 未找到对应的 JoinData，定义未查询到关联数据的情况
     *
     * @param data    源数据
     * @param joinKey 关联属性
     */
    protected abstract void onNotFound(SOURCE_DATA data, JOIN_KEY joinKey);

    List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> createJoinFieldContext(Collection<SOURCE_DATA> sourceDataCollection) {
        if (CollUtil.isEmpty(sourceDataCollection)) {
            return Collections.emptyList();
        }
        return sourceDataCollection.stream()
                .filter(Objects::nonNull)
                .filter(this::sourceDataFilter)
                .map(data -> new JoinFieldContext<>(this, data))
                .peek(context -> context.setJoinKey(joinKeyFromSourceData(context.getSourceData())))
                .filter(JoinFieldContext::notEmptyJoinKey)
                .distinct()
                .collect(toList());
    }

    Map<JOIN_KEY, List<JOIN_DATA>> createJoinDataMapping(Collection<JOIN_DATA> joinDataList) {
        final Map<Optional<JOIN_KEY>, List<JOIN_DATA>> joinDataMap = joinDataList.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(joinData -> Optional.ofNullable(joinKeyFromJoinData(joinData))));

        if (MapUtil.isEmpty(joinDataMap)) {
            log.debug("JoinExecutor-[{}] JoinDatMapping is empty", getName());
            return Collections.emptyMap();
        }
        final Map<JOIN_KEY, List<JOIN_DATA>> map = Maps.newHashMap();
        joinDataMap.forEach((k, v) -> {
            if (k.isEmpty() || CollUtil.isEmpty(v)) {
                return;
            }
            map.put(k.get(), v);
        });
        return map;
    }

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) return;
        // 针对 nested join 提供支持 since 1.1.0
        final List<SOURCE_DATA> list = sourceDataList.stream().flatMap(data -> extractSouceData(data).stream()).toList();
        // 这里的数据实际时根据嵌套层级展开后的数据
        doExecute(list);
    }

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList, MeterRegistry meterRegistry) {
        // 记录数据量指标
        Gauge.builder("join.field.data.size", sourceDataList::size)
                .description("Number of data items processed by grouped field executor")
                .tags("field", getName())
                .register(meterRegistry);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            execute(sourceDataList);
        } finally {
            sample.stop(
                    Timer.builder("join.fields.execution.time")
                            .tag("field", getName())
                            .register(meterRegistry)
            );
        }
    }

    void doExecute(Collection<SOURCE_DATA> sourceDataList) {
        // 构造上下文
        final List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> joinFieldContexts = createJoinFieldContext(sourceDataList);
        if (CollUtil.isEmpty(joinFieldContexts)) {
            log.debug("JoinExecutor-[{}] JoinFieldContext is empty", getName());
            return;
        }

        // 从源数据提取关联键
        final Set<JOIN_KEY> joinKeys = joinFieldContexts.stream()
                .map(JoinFieldContext::getJoinKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(joinKeys)) {
            log.debug("JoinExecutor-[{}] JoinKey is Empty", getName());
            return;
        }

        // 通过源数据提取的关联键, 提取关联数据
        final Collection<JOIN_DATA> joinDataCollection = joinDataByJoinKeys(joinKeys);
        if (CollUtil.isEmpty(joinDataCollection)) {
            log.debug("JoinExecutor-[{}] JoinData is Empty", getName());
            log.debug("Possible Reasons are: \n 1. Join Key from Datasource is Empty \n 2. Converted Join Key from Datasource is Empty ");
            return;
        }

        // 通过关联数据, 构造Mapping映射
        final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping = createJoinDataMapping(joinDataCollection);

        // 利用 TypeDescriptor 构造 JoinKey 的类型, 为了解决简单类型的转换, 比如'左表'键是个Long, '右表'键是String的情况
        final Optional<JOIN_KEY> first = joinDataMapping.keySet().stream().findFirst();
        assert first.isPresent();
        final TypeDescriptor targetType = TypeDescriptor.forObject(first.get());
        assert targetType != null;

        // 循环设置关联数据
        joinFieldContexts.forEach(context -> {
            final SOURCE_DATA sourceData = context.getSourceData();
            final JOIN_KEY joinKey = context.getJoinKey();

            // 通过标准转换操作, 将两侧的关联键类型转成一样的
            @SuppressWarnings("unchecked") final JOIN_KEY convertedJoinKey = (JOIN_KEY) STANDARD_TYPE_CONVERTER.convertValue(joinKey, TypeDescriptor.forObject(joinKey), targetType);

            // 获取关联数据
            final List<JOIN_DATA> mappingData = joinDataMapping.get(convertedJoinKey);

            // 找不到默认不做任何处理
            if (CollUtil.isEmpty(mappingData)) {
                log.debug("JoinExecutor-[{}] JoinData NotFound; JoinKey-[{}] ConvertedJoinKey-[{}]", getName(), joinKey, convertedJoinKey);
                onNotFound(sourceData, joinKey);
            } else {
                // 找到了, 默认:如果是集合设置到单个对象上, 那么集合元素必须=1, 否则异常
                final List<JOIN_RESULT> joinResults = mappingData.stream()
                        .filter(this::joinDataFilter)
                        .map(this::joinDataToJoinResult)
                        .filter(Objects::nonNull)
                        .collect(toList());
                onFound(sourceData, joinResults);
            }
        });
    }
}
