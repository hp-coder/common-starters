package com.hp.joininmemory;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public interface JoinFieldExecutor<DATA> {

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
     * @see com.hp.joininmemory.support.AbstractAnnotationBasedJoinFieldExecutorFactory#createName(Class, Field,
     * Annotation)
     */
    String getName();

    String getTargetFieldName();

    String getTargetClassName();
}
