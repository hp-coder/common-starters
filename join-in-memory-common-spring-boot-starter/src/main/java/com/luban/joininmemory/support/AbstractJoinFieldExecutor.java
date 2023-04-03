package com.luban.joininmemory.support;

import com.luban.joininmemory.JoinFieldExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public abstract class AbstractJoinFieldExecutor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinFieldExecutor<SOURCE_DATA> {

    /**
     * 从原始数据中生成 JoinKey
     *
     * @param data 原始数据对象
     * @return 关联属性值
     */
    protected abstract JOIN_KEY joinKeyFromSource(SOURCE_DATA data);

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
    protected abstract JOIN_KEY joinKeyFromJoinData(JOIN_DATA joinData);

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


    @Override
    public void execute(List<SOURCE_DATA> sourceDataList) {
        // 从源数据中提取 JoinKey
        List<JOIN_KEY> joinKeys = sourceDataList.stream()
                .filter(Objects::nonNull)
                .map(this::joinKeyFromSource)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());
        log.debug("get join key {} from source data {}", joinKeys, sourceDataList);

        // 根据 JoinKey 获取 JoinData
        List<JOIN_DATA> joinDataList = getJoinDataByJoinKeys(joinKeys);
        log.debug("get join data {} by join key {}", joinDataList, joinKeys);

        // 将 JoinData 以 Map 形式进行组织
        Map<JOIN_KEY, List<JOIN_DATA>> joinDataMap = joinDataList.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(this::joinKeyFromJoinData));
        log.debug("group by join key, result is {}", joinDataMap);

        sourceDataList.forEach(sourceData -> {
            // 从 SourceData 中 获取 JoinKey
            JOIN_KEY joinKey = joinKeyFromSource(sourceData);
            if (joinKey == null) {
                log.warn("join key from join data {} is null", sourceData);
            } else {
                // 根据 JoinKey 获取 JoinData
                List<JOIN_DATA> relations = joinDataMap.get(joinKey);
                if (CollectionUtils.isEmpty(relations)) {
                    log.warn("join data lost by join key {} for source data {}", joinKey, sourceData);
                    // 为获取到 JoinData，进行 notFound 回调
                    onNotFound(sourceData, joinKey);
                } else {
                    // 获取到 JoinData， 转换为 JoinResult，进行数据写回
                    List<JOIN_RESULT> joinResults = relations.stream()
                            .filter(Objects::nonNull)
                            .map(this::convertToResult)
                            .collect(toList());
                    log.debug("Successfully converted the join-data:{} to the join-result:{}", relations, joinResults);
                    onFound(sourceData, joinResults);
                    log.debug("Successfully wrote the join-result:{} to the source-data:{}", joinResults, sourceData);
                }
            }
        });
    }
}
