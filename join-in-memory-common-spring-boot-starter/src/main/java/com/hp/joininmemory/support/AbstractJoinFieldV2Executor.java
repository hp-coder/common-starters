package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.context.JoinFieldContext;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author hp 2024/1/8
 */
@Slf4j
public abstract class AbstractJoinFieldV2Executor<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

    /**
     * 从原始数据中生成 JoinKey
     *
     * @param data 原始数据对象
     * @return 关联属性值
     */
    protected abstract SOURCE_JOIN_KEY joinKeyFromSource(SOURCE_DATA data);

    /**
     * 提供对key的类型转换
     *
     * @param joinKey 关联key
     * @return 关联key值
     */
    protected abstract JOIN_KEY sourceJoinKeyToJoinKey(SOURCE_JOIN_KEY joinKey);

    /**
     * 根据 JoinKey 批量获取 JoinData
     *
     * @param joinKeys 关联属性值
     * @return 关联数据
     */
    protected abstract List<JOIN_DATA> joinDataByJoinKeys(Collection<JOIN_KEY> joinKeys);

    /**
     * 从 JoinData 中获取 JoinKey
     *
     * @param joinData 关联属性数据
     * @return 关联属性数据形成map时的key值
     */
    protected abstract DATA_JOIN_KEY dataJoinKeyFromJoinData(JOIN_DATA joinData);

    /**
     * 提供对key的类型转换
     *
     * @param joinKey 原始关联key
     * @return 转换后的关联key
     */
    protected abstract JOIN_KEY dataJoinKeyToJoinKey(DATA_JOIN_KEY joinKey);

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

    List<JoinFieldContext<
                SOURCE_DATA,
                SOURCE_JOIN_KEY,
                JOIN_KEY,
                JOIN_DATA,
                DATA_JOIN_KEY,
                JOIN_RESULT
                >> createJoinFieldContext(Collection<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) {
            return Collections.emptyList();
        }
        return sourceDataList.stream()
                .filter(Objects::nonNull)
                .map(data -> new JoinFieldContext<>(this, data))
                .peek(context -> context.setSourceJoinKey(joinKeyFromSource(context.getSourceData())))
                .filter(JoinFieldContext::notEmptySourceJoinKey)
                .peek(context -> context.setJoinKey(sourceJoinKeyToJoinKey(context.getSourceJoinKey())))
                .filter(JoinFieldContext::notEmptyJoinKey)
                .distinct()
                .collect(toList());
    }

    @Nullable
    Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping(List<JOIN_DATA> joinDataList) {
        final Map<Optional<JOIN_KEY>, List<JOIN_DATA>> joinDataMap = joinDataList.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(joinData -> {
                    final DATA_JOIN_KEY dataJoinKey = dataJoinKeyFromJoinData(joinData);
                    if (Objects.isNull(dataJoinKey)) {
                        return Optional.empty();
                    }
                    final JOIN_KEY joinKey = dataJoinKeyToJoinKey(dataJoinKey);
                    return Optional.ofNullable(joinKey);
                }));
        if (CollUtil.isEmpty(joinDataMap)) {
            log.warn("Join data from the datasource is empty. Abort Join!");
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

    @Override
    public void execute(Collection<SOURCE_DATA> sourceDataList) {
        try {
            if (CollUtil.isEmpty(sourceDataList)) {
                log.warn("The given source data is empty. Abort Join!");
                return;
            }
            final List<JoinFieldContext<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT>> joinContexts =
                    createJoinFieldContext(sourceDataList);
            if (CollUtil.isEmpty(joinContexts)) {
                log.warn("Join contexts are empty. Abort Join!");
                return;
            }
            final Set<JOIN_KEY> joinKeys = joinContexts.stream()
                    .map(JoinFieldContext::getJoinKey)
                    .collect(Collectors.toSet());
            if (CollUtil.isEmpty(joinKeys)) {
                log.warn("Join keys from source are empty. Abort Join!");
                return;
            }
            final List<JOIN_DATA> joinDataList = joinDataByJoinKeys(joinKeys);
            if (CollUtil.isEmpty(joinKeys)) {
                log.warn("Join data list from datasource is empty. Abort Join!");
                return;
            }
            final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMapping = joinDataMapping(joinDataList);
            if (CollUtil.isEmpty(joinDataMapping)) {
                log.warn("Join data mapping from datasource is empty. Abort Join!");
                log.warn("Possible reasons are: \n 1. join keys from datasource are all null; \n 2. converted join keys from datasource are all null;");
                return;
            }
            log.debug("Starting join process");
            joinContexts.forEach(context -> {
                final SOURCE_DATA sourceData = context.getSourceData();
                final JOIN_KEY joinKey = context.getJoinKey();
                final List<JOIN_DATA> mappingData = joinDataMapping.get(joinKey);
                if (CollUtil.isEmpty(mappingData)) {
                    log.warn("Join data can't be found through the join key {}", joinKey);
                    onNotFound(sourceData, joinKey);
                } else {
                    final List<JOIN_RESULT> joinResults = mappingData.stream()
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
