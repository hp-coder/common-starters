package com.hp.joininmemory;

import org.springframework.core.annotation.MergedAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public interface JoinFieldExecutorGrouper<A extends Annotation, KEY> {

    <DATA> Function<MergedAnnotation<?>, KEY> groupBy(Class<DATA> clazz, Field field, A annotation);
}
