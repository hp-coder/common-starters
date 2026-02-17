package com.hp.joininmemory;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.Collection;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface JoinFieldsExecutor<DATA> {

    /**
     * 调用被处理对象的属性装载逻辑，这里用于指定串并行场景
     */
    void execute(Collection<DATA> data);

    /**
     * 调用被处理对象的属性装载逻辑，这里用于指定串并行场景, 支持指标记录
     */
    void execute(Collection<DATA> data, MeterRegistry meterRegistry);

}
