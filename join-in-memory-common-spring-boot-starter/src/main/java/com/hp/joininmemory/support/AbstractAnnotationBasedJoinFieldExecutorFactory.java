package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.JoinFieldExecutorFactory;
import com.hp.joininmemory.JoinFieldExecutorGrouper;
import com.hp.joininmemory.context.JoinContext;
import com.hp.joininmemory.context.JoinFieldExecutorContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author hp 2023/3/27
 */
public abstract class AbstractAnnotationBasedJoinFieldExecutorFactory<A extends Annotation> implements JoinFieldExecutorFactory, JoinFieldExecutorGrouper<A> {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedJoinFieldExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final Class<DATA> clazz = context.getDataClass();
        final List<JoinFieldExecutorContext<DATA, A>> joinFieldExecutorContexts = createJoinFieldExecutorContext(clazz, context);
        return joinFieldExecutorContexts.stream()
                .filter(Objects::nonNull)
                .map(JoinFieldExecutorContext::getExecutor)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutorContext<DATA, A>> createJoinFieldExecutorContext(Class<DATA> clazz, JoinContext<DATA> context) {
        if (context.getConfig().processPolicy().isGrouped()) {
            return createGroupedJoinFieldExecutorContext(clazz);
        } else {
            return createJoinFieldExecutorContext(clazz);
        }
    }

    private <DATA,
            SOURCE_JOIN_KEY,
            JOIN_KEY,
            JOIN_DATA,
            DATA_JOIN_KEY,
            JOIN_RESULT> List<JoinFieldExecutorContext<DATA, A>> createGroupedJoinFieldExecutorContext(Class<DATA> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }

        return fields.stream()
                .filter(field -> AnnotatedElementUtils.isAnnotated(field, annotationClass))
                .collect(groupingBy(field -> groupBy(clazz, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)).apply(null)))
                .values()
                .stream()
                .map(groupedFields -> {
                    final JoinFieldExecutorContext<DATA, A> context = new JoinFieldExecutorContext<>();
                    final DefaultGroupedJoinFieldExecutor<DATA,
                            SOURCE_JOIN_KEY,
                            JOIN_KEY,
                            JOIN_DATA,
                            DATA_JOIN_KEY,
                            JOIN_RESULT> groupedExecutor = new DefaultGroupedJoinFieldExecutor<>(
                            groupedFields.stream()
                                    .map(field -> this.<DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT>
                                            buildJoinFieldExecutor(clazz, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)))
                                    .collect(Collectors.toList())
                    );
                    context.setExecutor(groupedExecutor);
                    return context;
                })
                .collect(Collectors.toList());
    }

    private <DATA> List<JoinFieldExecutorContext<DATA, A>> createJoinFieldExecutorContext(Class<DATA> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        if (CollUtil.isEmpty(fields)) {
            return Collections.emptyList();
        }
        return fields.stream()
                .filter(f -> AnnotatedElementUtils.isAnnotated(f, annotationClass))
                .map(field -> {
                    final JoinFieldExecutorContext<DATA, A> context = new JoinFieldExecutorContext<>();
                    context.setField(field);
                    final A annotation = AnnotatedElementUtils.getMergedAnnotation(field, annotationClass);
                    context.setAnnotation(annotation);
                    context.setExecutor(buildJoinFieldExecutor(clazz, field, annotation));
                    return context;
                })
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
