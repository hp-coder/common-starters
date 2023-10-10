package com.luban.joininmemory;

/**
 * @author hp
 */
public interface AfterJoinMethodExecutor<DATA_AFTER_JOIN> {

    /**
     * 对应关联属性的数据查询，转换，装载逻辑
     *
     * @param data 数据集合
     */
    void execute(DATA_AFTER_JOIN data);

    /**
     * 执行顺序, 越低越优先
     *
     * @return 任务执行顺序
     */
    default int runOnOrder() {
        return 0;
    }

    /**
     * 执行层级，用于并行任务的分类, 越低越优先
     *
     * @return 任务层级
     */
    default int runOnLevel() {
        return 0;
    }
}
