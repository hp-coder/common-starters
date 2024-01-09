package com.hp.joininmemory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @author hp
 */
public interface JoinFieldExecutorGrouper<A extends Annotation> {

   <DATA> Function<Object, Object> groupBy(Class<DATA> clazz, Field field, A annotation);
}
