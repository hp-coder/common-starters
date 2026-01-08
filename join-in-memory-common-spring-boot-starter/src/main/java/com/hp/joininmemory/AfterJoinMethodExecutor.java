package com.hp.joininmemory;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public interface AfterJoinMethodExecutor<DATA_AFTER_JOIN> {

    /**
     * 对应关联属性的数据查询，转换，装载逻辑
     *
     * @param data 数据集合
     */
    void execute(DATA_AFTER_JOIN data);

    /**
     * Metrics support
     */
    void execute(DATA_AFTER_JOIN data, MeterRegistry meterRegistry);

    /**
     * 执行层级，用于并行任务的分类, 越低越优先
     *
     * @return 任务层级
     */
    default int runOnLevel() {
        return 0;
    }
}
