package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.JoinFieldExecutorFactory;
import com.hp.joininmemory.JoinFieldExecutorGrouper;
import com.hp.joininmemory.context.JoinContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public abstract class AbstractAnnotationBasedJoinFieldExecutorFactory<A extends Annotation> implements JoinFieldExecutorFactory, JoinFieldExecutorGrouper<A, String> {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedJoinFieldExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final List<JoinFieldExecutor<DATA>> executors = createJoinFieldExecutor(context);
        return executors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createJoinFieldExecutor(JoinContext<DATA> context) {
        if (context.getConfig().fieldProcessPolicy().isGrouped()) {
            return createGroupedJoinFieldExecutor(context);
        } else {
            return createSeparatedJoinFieldExecutor(context);
        }
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createGroupedJoinFieldExecutor(JoinContext<DATA> context) {
        final Class<DATA> clazz = Objects.requireNonNull(context.getDataClass());
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }
        return fields.stream()
                .filter(field -> AnnotatedElementUtils.isAnnotated(field, annotationClass))
                .collect(groupingBy(field -> {
                    final A mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, annotationClass);
                    final MergedAnnotation<?> rootAnnotation = getRootAnnotation(field);
                    return groupBy(clazz, field, mergedAnnotation).apply(rootAnnotation);
                }))
                .values()
                .stream()
                .map(groupedFields -> (JoinFieldExecutor<DATA>) new DefaultGroupedJoinFieldExecutor<>(
                        groupedFields.stream()
                                .map(field -> this.createJoinFieldExecutor(context, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)))
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(JoinFieldExecutor::runOnLevel))
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutor<DATA>> createSeparatedJoinFieldExecutor(JoinContext<DATA> context) {
        final Class<DATA> clazz = Objects.requireNonNull(context.getDataClass());
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }
        return fields.stream()
                .filter(f -> AnnotatedElementUtils.isAnnotated(f, annotationClass))
                .map(field -> createJoinFieldExecutor(context, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)))
                .collect(Collectors.toList());
    }

    protected <DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>
    AbstractJoinFieldV2Executor<DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> createJoinFieldExecutor(JoinContext<DATA> context, Field field, A annotation) {
        if (annotation == null) {
            return null;
        }
        final Class<DATA> clazz = context.getDataClass();
        return new DefaultJoinFieldExecutorAdaptor<>(
                createName(clazz, field, annotation),
                createRunLevel(clazz, field, annotation),
                createSourceDataFilter(clazz, field, annotation),
                createKeyFromSourceData(clazz, field, annotation),
                createLoader(clazz, field, annotation),
                createKeyFromJoinData(clazz, field, annotation),
                createJoinDataFilter(clazz, field, annotation),
                createJoinDataConverter(clazz, field, annotation),
                createFoundFunction(clazz, field, annotation),
                createLostFunction(clazz, field, annotation)
        );
    }

    protected Map<String, Method> getNonMetaAnnotationAttributeMethods(MergedAnnotation<?> rootAnnotation) {
        return Arrays.stream(rootAnnotation.getType().getDeclaredMethods())
                .filter(i -> {
                    final AliasFor aliasFor = i.getAnnotation(AliasFor.class);
                    return aliasFor == null || !aliasFor.annotation().equals(this.annotationClass);
                })
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    protected List<Map.Entry<String, Object>> getNonMetaAnnotationAttributes(MergedAnnotation<?> rootAnnotation) {
        final Map<String, Method> nonMetaAnnotationAttributeMethods = getNonMetaAnnotationAttributeMethods(rootAnnotation);
        return rootAnnotation.asAnnotationAttributes()
                .entrySet()
                .stream()
                .filter(i -> nonMetaAnnotationAttributeMethods.containsKey(i.getKey()))
                .filter(i -> Objects.nonNull(i.getValue()))
                .filter(i -> StrUtil.isNotEmpty(i.getValue().toString()))
                .collect(Collectors.toList());
    }

    protected MergedAnnotation<?> getRootAnnotation(Field field) {
        final MergedAnnotations from = MergedAnnotations.from(field, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
        return from.get(this.annotationClass).getRoot();
    }

    protected <DATA> String createName(Class<DATA> clazz, Field field, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#field[" + field.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA> int createRunLevel(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<DATA, Boolean> createSourceDataFilter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, SOURCE_JOIN_KEY> Function<DATA, SOURCE_JOIN_KEY> createKeyFromSourceData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, DATA_JOIN_KEY> Function<JOIN_DATA, DATA_JOIN_KEY> createKeyFromJoinData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA> Function<JOIN_DATA, Boolean> createJoinDataFilter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA, JOIN_RESULT> BiConsumer<DATA, Collection<JOIN_RESULT>> createFoundFunction(Class<DATA> clazz, Field field, A annotation);

    protected <DATA, JOIN_KEY> BiConsumer<DATA, JOIN_KEY> createLostFunction(Class<DATA> clazz, Field field, A annotation) {
        return (data, joinKey) -> {
        };
    }
}
