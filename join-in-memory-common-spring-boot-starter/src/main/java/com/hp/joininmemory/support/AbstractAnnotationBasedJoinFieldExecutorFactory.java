package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.JoinFieldExecutorFactory;
import com.hp.joininmemory.JoinFieldExecutorGrouper;
import com.hp.joininmemory.context.JoinContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author hp 2023/3/27
 */
public abstract class AbstractAnnotationBasedJoinFieldExecutorFactory<A extends Annotation> implements JoinFieldExecutorFactory, JoinFieldExecutorGrouper<A,String> {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedJoinFieldExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final Class<DATA> clazz = context.getDataClass();
        final List<JoinFieldExecutor<DATA>> executors = createJoinFieldExecutor(clazz, context);
        return executors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createJoinFieldExecutor(Class<DATA> clazz, JoinContext<DATA> context) {
        if (context.getConfig().fieldProcessPolicy().isGrouped()) {
            return createGroupedJoinFieldExecutor(clazz);
        } else {
            return createJoinFieldExecutor(clazz);
        }
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createGroupedJoinFieldExecutor(Class<DATA> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }
        final Map<String, List<Field>> maps = fields.stream()
                .filter(field -> AnnotatedElementUtils.isAnnotated(field, annotationClass))
                .collect(groupingBy(field -> {
                    final A mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, annotationClass);
                    // Reduce the possibility of different annotations being grouped togther.
                    final MergedAnnotations from = MergedAnnotations.from(field, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
                    final MergedAnnotation<?> rootAnnotation = from.get(annotationClass).getRoot();
                    return groupBy(clazz, field, mergedAnnotation).apply(rootAnnotation.getType());
                }));
        return maps
                .values()
                .stream()
                .map(groupedFields -> (JoinFieldExecutor<DATA>) new DefaultGroupedJoinFieldExecutor<>(
                        groupedFields.stream()
                                .map(field -> this.buildJoinFieldExecutor(clazz, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createJoinFieldExecutor(Class<DATA> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }
        return fields.stream()
                .filter(f -> AnnotatedElementUtils.isAnnotated(f, annotationClass))
                .map(field -> buildJoinFieldExecutor(clazz, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)))
                .collect(Collectors.toList());
    }

    private <DATA,
            SOURCE_JOIN_KEY,
            JOIN_KEY,
            JOIN_DATA,
            DATA_JOIN_KEY,
            JOIN_RESULT>
    AbstractJoinFieldV2Executor<
            DATA,
            SOURCE_JOIN_KEY,
            JOIN_KEY,
            JOIN_DATA,
            DATA_JOIN_KEY,
            JOIN_RESULT> buildJoinFieldExecutor(Class<DATA> clazz, Field field, A annotation) {
        if (annotation == null) {
            return null;
        }
        return new DefaultJoinFieldExecutorAdaptor<>(
                createName(clazz, field, annotation),
                createRunLevel(clazz, field, annotation),
                createKeyFromSourceData(clazz, field, annotation),
                createSourceDataKeyConverter(clazz, field, annotation),
                createLoader(clazz, field, annotation),
                createKeyFromJoinData(clazz, field, annotation),
                createJoinDataKeyConverter(clazz, field, annotation),
                createJoinDataConverter(clazz, field, annotation),
                createFoundFunction(clazz, field, annotation),
                createLostFunction(clazz, field, annotation)
        );
    }

    protected <DATA> String createName(Class<DATA> clazz, Field field, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#field[" + field.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA> int createRunLevel(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, SOURCE_JOIN_KEY> Function<DATA, SOURCE_JOIN_KEY> createKeyFromSourceData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, SOURCE_JOIN_KEY, JOIN_KEY> Function<SOURCE_JOIN_KEY, JOIN_KEY> createSourceDataKeyConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, DATA_JOIN_KEY> Function<JOIN_DATA, DATA_JOIN_KEY> createKeyFromJoinData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_KEY, DATA_JOIN_KEY> Function<DATA_JOIN_KEY, JOIN_KEY> createJoinDataKeyConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_RESULT> BiConsumer<DATA, List<JOIN_RESULT>> createFoundFunction(Class<DATA> clazz, Field field, A annotation);

    protected <DATA, JOIN_KEY> BiConsumer<DATA, JOIN_KEY> createLostFunction(Class<DATA> clazz, Field field, A annotation) {
        return null;
    }

}
