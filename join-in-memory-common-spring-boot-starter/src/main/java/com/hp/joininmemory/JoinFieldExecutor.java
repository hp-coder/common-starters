package com.hp.joininmemory;

import java.util.Collection;

/**
 * @author hp
 */
public interface JoinFieldExecutor<DATA> {

    /**
     * 对应关联属性的数据查询，转换，装载逻辑
     *
     * @param dataList 数据集合
     */
    void execute(Collection<DATA> dataList);

    /**
     * 执行层级，用于并行任务的分类
     *
     * @return 执行层级
     */
    default int runOnLevel() {
        return 0;
    }
}
