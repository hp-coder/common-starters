package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.context.JoinFieldContext;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author hp 2024/1/8
 */
@Slf4j
public abstract class AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

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
     * @return 关联属性数据形成map时的key值
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

    @Nullable
    Map<JOIN_KEY, List<JOIN_DATA>> createJoinDataMapping(Collection<JOIN_DATA> joinDataList) {
        final Map<Optional<JOIN_KEY>, List<JOIN_DATA>> joinDataMap = joinDataList.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(joinData -> Optional.ofNullable(joinKeyFromJoinData(joinData))));

        if (MapUtil.isEmpty(joinDataMap)) {
            log.trace("Join data from the datasource is empty. Abort Join!");
            return null;
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

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList) {
        try {
            if (CollUtil.isEmpty(sourceDataList)) {
                log.trace("The given source data is empty. Abort Join!");
                return;
            }
            final List<JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> joinContexts =
                    createJoinFieldContext(sourceDataList);
            if (CollUtil.isEmpty(joinContexts)) {
                log.trace("Join field contexts are empty. Abort Join!");
                return;
            }
            final Set<JOIN_KEY> joinKeys = joinContexts.stream()
                    .map(JoinFieldContext::getJoinKey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (CollUtil.isEmpty(joinKeys)) {
                log.trace("Join keys from source are empty. Abort Join!");
                return;
            }

            final Collection<JOIN_DATA> joinDataList = joinDataByJoinKeys(joinKeys);
            if (CollUtil.isEmpty(joinKeys)) {
                log.trace("Join data list from datasource is empty. Abort Join!");
                return;
            }
            final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping = createJoinDataMapping(joinDataList);
            if (CollUtil.isEmpty(joinDataMapping)) {
                log.trace("Join data mapping from datasource is empty. Abort Join!");
                log.trace("Possible reasons are: \n 1. join keys from datasource are all null; \n 2. converted join keys from datasource are all null;");
                return;
            }

            final Optional<JOIN_KEY> first = joinDataMapping.keySet().stream().findFirst();
            assert first.isPresent();
            final TypeDescriptor targetType = TypeDescriptor.forObject(first.get());
            assert targetType != null;

            log.trace("Starting join process");
            joinContexts.forEach(context -> {
                final SOURCE_DATA sourceData = context.getSourceData();
                final JOIN_KEY joinKey = context.getJoinKey();
                final JOIN_KEY convertedJoinKey = (JOIN_KEY) STANDARD_TYPE_CONVERTER.convertValue(joinKey, TypeDescriptor.forObject(joinKey), targetType);
                final List<JOIN_DATA> mappingData = joinDataMapping.get(convertedJoinKey);
                if (CollUtil.isEmpty(mappingData)) {
                    log.trace("Join data can't be found through the join key {}", joinKey);
                    onNotFound(sourceData, joinKey);
                } else {
                    final List<JOIN_RESULT> joinResults = mappingData.stream()
                            .filter(this::joinDataFilter)
                            .map(this::joinDataToJoinResult)
                            .filter(Objects::nonNull)
                            .collect(toList());
                    onFound(sourceData, joinResults);
                }
            });
        } catch (Exception e) {
            throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
        }
    }
}
