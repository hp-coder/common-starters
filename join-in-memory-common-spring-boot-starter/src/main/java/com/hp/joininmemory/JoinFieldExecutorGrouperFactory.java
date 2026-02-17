package com.hp.joininmemory;

import com.hp.joininmemory.context.JoinContext;
import org.springframework.core.annotation.MergedAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface JoinFieldExecutorGrouperFactory<A extends Annotation, KEY> {

    /**
     * Join Field Grouping Strategy
     */
    <DATA> Function<MergedAnnotation<?>, KEY> groupBy(JoinContext<DATA> context, Field field, A annotation);
}
