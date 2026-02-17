package com.hp.joininmemory;

import com.hp.joininmemory.support.AbstractAnnotationBasedJoinFieldExecutorFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.util.Collection;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface JoinFieldExecutor<DATA> extends JoinFieldGrouper {

    StandardTypeConverter STANDARD_TYPE_CONVERTER = new StandardTypeConverter();

    /**
     * 对应关联属性的数据查询，转换，装载逻辑
     *
     * @param dataList 数据集合
     */
    void execute(Collection<DATA> dataList);

    /**
     * Metrics support
     */
    void execute(Collection<DATA> dataList, MeterRegistry meterRegistry);

    /**
     * 执行层级，用于并行任务的分类
     *
     * @return 执行层级
     */
    default int runOnLevel() {
        return 0;
    }

    /**
     * The name of the join field executor
     *
     * @see AbstractAnnotationBasedJoinFieldExecutorFactory
     * AbstractAnnotationBasedJoinFieldExecutorFactory.createName(Class, Field, Annotation)
     */
    String getName();

    /**
     * The actual field that is being joined. It can be a nested field.
     */
    String getTargetFieldName();

    /**
     * The class of the target field. In nested join operations, it is the root class name
     */
    String getTargetClassName();
}
