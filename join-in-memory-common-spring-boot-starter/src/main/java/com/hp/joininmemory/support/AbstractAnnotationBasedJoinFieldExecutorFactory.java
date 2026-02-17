package com.hp.joininmemory.support;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.JoinFieldExecutorFactory;
import com.hp.joininmemory.JoinFieldExecutorGrouperFactory;
import com.hp.joininmemory.JoinFieldGrouper;
import com.hp.joininmemory.annotation.NestedJoin;
import com.hp.joininmemory.context.JoinContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public abstract class AbstractAnnotationBasedJoinFieldExecutorFactory<A extends Annotation> implements JoinFieldExecutorFactory, JoinFieldExecutorGrouperFactory<A, String> {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedJoinFieldExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final List<? extends JoinFieldExecutor<DATA>> executors = createJoinFieldExecutor(context);
        return executors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <DATA> List<? extends JoinFieldExecutor<DATA>> createJoinFieldExecutor(JoinContext<DATA> context) {
        if (context.getConfig().fieldProcessPolicy().isGrouped()) {
            return createGroupedJoinFieldExecutor(context);
        } else {
            return createSeparatedJoinFieldExecutor(context);
        }
    }

    private <DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> List<JoinFieldExecutor<DATA>> createGroupedJoinFieldExecutor(JoinContext<DATA> context) {
        List<AbstractJoinFieldV2Executor<DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> executors = Lists.newArrayList();
        createNestedJoinFieldExecutor(context, executors);
        return executors.stream()
                .collect(groupingBy(JoinFieldGrouper::groupingKey))
                .values()
                .stream()
                .map(DefaultGroupedJoinFieldExecutor::new)
                .sorted(Comparator.comparing(JoinFieldExecutor::runOnLevel))
                .collect(Collectors.toList());
    }

    private <DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> List<? extends JoinFieldExecutor<DATA>> createSeparatedJoinFieldExecutor(JoinContext<DATA> context) {
        List<AbstractJoinFieldV2Executor<DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> holder = Lists.newArrayList();
        createNestedJoinFieldExecutor(context, holder);
        return holder;
    }

    private <DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> void createNestedJoinFieldExecutor(JoinContext<DATA> context, List<AbstractJoinFieldV2Executor<DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT>> holder) {
        final Class<?>[] clazz = {context.getCurrentClass()};
        final List<Field> fields = context.getCurrentClassFields();
        fields.forEach(field -> {
            if (AnnotatedElementUtils.isAnnotated(field, NestedJoin.class)) {
                if (JoinContext.invalidFieldType(clazz, field)) {
                    return;
                }
                context.enter(field, clazz[0]);
                createNestedJoinFieldExecutor(context, holder);
            }
            // 递归从这里出来. 所以 clazz 并不会被更新
            if (AnnotatedElementUtils.isAnnotated(field, annotationClass)) {
                holder.add(createJoinFieldExecutor(context, field, AnnotatedElementUtils.getMergedAnnotation(field, annotationClass)));
            }
        });
        context.exit();
    }

    protected <DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> AbstractJoinFieldV2Executor<DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> createJoinFieldExecutor(JoinContext<DATA> context, Field field, A annotation) {
        if (annotation == null) {
            return null;
        }

        return new DefaultJoinFieldExecutorAdaptor<>(
                createName(context, field, annotation),
                createRunLevel(context, field, annotation),
                context.getHierarchicalPaths(),
                createExtractSourceData(context, field, annotation),
                createSourceDataFilter(context, field, annotation),
                createKeyFromSourceData(context, field, annotation),
                createLoader(context, field, annotation),
                createKeyFromJoinData(context, field, annotation),
                createJoinDataFilter(context, field, annotation),
                createJoinDataConverter(context, field, annotation),
                createFoundFunction(context, field, annotation),
                createLostFunction(context, field, annotation),
                groupBy(context, field, annotation).apply(getRootAnnotation(field))
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

    protected <DATA> String createName(JoinContext<DATA> context, Field field, A annotation) {
        return "class[" + context.getDataClass().getSimpleName() + "]" +
                "#field[" + context.getHierarchicalPath(field) + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA> BiFunction<DATA, List<String>, Collection<DATA>> createExtractSourceData(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA> int createRunLevel(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA> Function<DATA, Boolean> createSourceDataFilter(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, SOURCE_JOIN_KEY> Function<DATA, SOURCE_JOIN_KEY> createKeyFromSourceData(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, DATA_JOIN_KEY> Function<JOIN_DATA, DATA_JOIN_KEY> createKeyFromJoinData(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA> Function<JOIN_DATA, Boolean> createJoinDataFilter(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(JoinContext<DATA> context, Field field, A annotation);

    protected abstract <DATA, JOIN_RESULT> BiConsumer<DATA, Collection<JOIN_RESULT>> createFoundFunction(JoinContext<DATA> context, Field field, A annotation);

    protected <DATA, JOIN_KEY> BiConsumer<DATA, JOIN_KEY> createLostFunction(JoinContext<DATA> context, Field field, A annotation) {
        return (data, joinKey) -> {
        };
    }
}
