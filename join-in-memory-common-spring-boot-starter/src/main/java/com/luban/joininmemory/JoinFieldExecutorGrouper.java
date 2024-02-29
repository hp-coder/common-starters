package com.luban.joininmemory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @author hp
 */
public interface JoinFieldExecutorGrouper<A extends Annotation, KEY> {

   <DATA> Function<Class<? extends Annotation>, KEY> groupBy(Class<DATA> clazz, Field field, A annotation);
}
