package com.luban.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import com.luban.joininmemory.JoinFieldExecutor;
import com.luban.joininmemory.exception.JoinErrorCode;
import com.luban.joininmemory.exception.JoinException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author hp 2023/3/27
 */
@Slf4j
@Deprecated(forRemoval = true)
public abstract class AbstractJoinFieldExecutor<SOURCE_DATA, ROW_JOIN_KEY, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

    /**
     * 从原始数据中生成 JoinKey
     *
     * @param data 原始数据对象
     * @return 关联属性值
     */
    protected abstract ROW_JOIN_KEY joinKeyFromSource(SOURCE_DATA data);

    /**
     * 提供对key的类型转换
     *
     * @param joinKey 关联key
     * @return 关联key值
     */
    protected abstract JOIN_KEY convertJoinKeyFromSourceData(ROW_JOIN_KEY joinKey);

    /**
     * 根据 JoinKey 批量获取 JoinData
     *
     * @param joinKeys 关联属性值
     * @return 关联数据
     */
    protected abstract List<JOIN_DATA> getJoinDataByJoinKeys(List<JOIN_KEY> joinKeys);

    /**
     * 从 JoinData 中获取 JoinKey
     *
     * @param joinData 关联属性数据
     * @return 关联属性数据形成map时的key值
     */
    protected abstract ROW_JOIN_KEY joinKeyFromJoinData(JOIN_DATA joinData);

    /**
     * 提供对key的类型转换
     *
     * @param joinKey 原始关联key
     * @return 转换后的关联key
     */
    protected abstract JOIN_KEY convertJoinKeyFromJoinData(ROW_JOIN_KEY joinKey);

    /**
     * 将 JoinData 转换为 JoinResult
     *
     * @param joinData 关联数据
     * @return 转换后的数据结果，如：entity -> VO
     */
    protected abstract JOIN_RESULT convertToResult(JOIN_DATA joinData);

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

    private List<JOIN_KEY> joinKeys(List<SOURCE_DATA> sourceDataList) {
        if (CollUtil.isEmpty(sourceDataList)) {
            return Collections.emptyList();
        }
        return sourceDataList.stream()
                .filter(Objects::nonNull)
                .map(this::joinKeyFromSource)
                .filter(Objects::nonNull)
                .map(this::convertJoinKeyFromSourceData)
                //提取转换之后, 类型应该都相同
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());
    }

    @Override
    public void execute(List<SOURCE_DATA> sourceDataList) {
        try {
            // 从源数据中提取 JoinKey
            final List<JOIN_KEY> joinKeys = joinKeys(sourceDataList);
            log.debug("Join keys are {}", joinKeys);
            if (CollUtil.isEmpty(joinKeys)) {
                log.warn("The given source data is empty. Abort!");
                return;
            }
            final List<JOIN_DATA> joinDataList = getJoinDataByJoinKeys(joinKeys);
            log.debug("Join records are {}", joinDataList);

            final Map<JOIN_KEY, List<JOIN_DATA>> joinDataMap = joinDataList.stream()
                    .filter(Objects::nonNull)
                    .collect(groupingBy(joinKey -> convertJoinKeyFromJoinData(joinKeyFromJoinData(joinKey))));
            sourceDataList.forEach(sourceData -> {
                final JOIN_KEY joinKey = convertJoinKeyFromSourceData(joinKeyFromSource(sourceData));
                log.debug("Using join key {}", joinKey);
                if (joinKey == null) {
                    return;
                }
                final List<JOIN_DATA> relations = joinDataMap.get(joinKey);
                if (CollUtil.isEmpty(relations)) {
                    log.debug("Join results can't be found through the join key {}", joinKey);
                    onNotFound(sourceData, joinKey);
                } else {
                    // 获取到 JoinData， 转换为 JoinResult，进行数据写回
                    final List<JOIN_RESULT> joinResults = relations.stream()
                            .filter(Objects::nonNull)
                            .map(this::convertToResult)
                            .filter(Objects::nonNull)
                            .collect(toList());
                    log.debug("Join results are {}", joinResults);
                    onFound(sourceData, joinResults);
                }
            });
        } catch (Exception e) {
            log.error("JoinErrorMessage={}", JoinErrorCode.JOIN_ERROR.getName(), e);
            throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
        }
    }
}
