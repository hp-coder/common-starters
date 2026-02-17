package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.annotation.JoinInMemory;
import com.hp.joininmemory.constant.ExecuteLevel;
import com.hp.joininmemory.context.JoinContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public class JoinInMemoryBasedJoinFieldExecutorFactory extends AbstractAnnotationBasedJoinFieldExecutorFactory<JoinInMemory> {

    private final SpELHelper spELHelper;

    public JoinInMemoryBasedJoinFieldExecutorFactory(SpELHelper spELHelper) {
        super(JoinInMemory.class);
        this.spELHelper = spELHelper;
    }

    @SuppressWarnings("unchecked")
    static <DATA> Collection<DATA> goDownThePath(SpELHelper spELHelper, LinkedList<String> hierarchicalPaths, Collection<DATA> dataCollection) {
        if (CollUtil.isEmpty(hierarchicalPaths)) {
            return dataCollection;
        }
        final String path = hierarchicalPaths.pollFirst();
        log.trace("Go down the path: the path is {}", path);
        final List<DATA> list = dataCollection.stream()
                .flatMap(data -> {
                    final Object apply = spELHelper.newGetterInstance(path).apply(data);
                    if (Objects.isNull(apply)) {
                        return Stream.empty();
                    }

                    Collection<DATA> extracted;
                    if (Collection.class.isAssignableFrom(apply.getClass())) {
                        extracted = (Collection<DATA>) apply;
                    } else {
                        extracted = List.of((DATA) apply);
                    }

                    if (CollUtil.isEmpty(hierarchicalPaths)) return extracted.stream();

                    return goDownThePath(spELHelper, hierarchicalPaths, extracted).stream();
                })
                .toList();
        log.trace("Go down the path: the extracted data is {}", list);
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <DATA> BiFunction<DATA, List<String>, Collection<DATA>> createExtractSourceData(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        return (data, hierarchicalPaths) -> {
            final LinkedList<String> modifiablePaths = Lists.newLinkedList(hierarchicalPaths);
            Collection<DATA> dataCollection;
            if (Collection.class.isAssignableFrom(data.getClass())) {
                dataCollection = (Collection<DATA>) data;
            } else {
                dataCollection = Lists.newArrayList(data);
            }
            if (CollUtil.isEmpty(hierarchicalPaths)) return dataCollection;
            return goDownThePath(spELHelper, modifiablePaths, dataCollection);
        };
    }

    @Override
    protected <DATA> int createRunLevel(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        int actualRunLevel = getActualRunLevel(context, annotation);
        log.trace("JoinField-c[{}]-f[{}] runs on level {}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), actualRunLevel);
        return actualRunLevel;
    }

    private <DATA> int getActualRunLevel(JoinContext<DATA> context, JoinInMemory annotation) {
        return context.getRunLevel(f -> {
                    final JoinInMemory mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(f, annotationClass);
                    return Optional.ofNullable(mergedAnnotation).map(i -> i.runLevel().getCode()).orElse(ExecuteLevel.FIFTH.getCode());
                },
                annotation.runLevel().getCode()
        );
    }

    @Override
    protected <DATA> Function<DATA, Boolean> createSourceDataFilter(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        if (StrUtil.isEmpty(annotation.sourceDataFilter())) {
            log.trace("JoinField-c[{}]-f[{}]: sourceDataFilter is empty", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field));
            return data -> Boolean.TRUE;
        }
        log.trace("JoinField-c[{}]-f[{}]: sourceDataFilter={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), annotation.sourceDataFilter());
        return spELHelper.newGetterInstance(annotation.sourceDataFilter());
    }

    @Override
    protected <DATA, JOIN_KEY> Function<DATA, JOIN_KEY> createKeyFromSourceData(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        log.trace("JoinField-c[{}]-f[{}]: keyFromSourceData={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), annotation.keyFromJoinData());
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.keyFromSourceData()), "The keyFromSourceData on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        return spELHelper.newGetterInstance(annotation.keyFromSourceData());
    }

    @Override
    protected <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.loader()), "The loader on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        final AtomicReference<String> loadSpEL = new AtomicReference<>(annotation.loader());
        log.trace("JoinField-c[{}]-f[{}]: loader={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), loadSpEL.get());

        getNonMetaAnnotationAttributes(getRootAnnotation(field)).forEach(i -> loadSpEL.set(loadSpEL.get().replace("#%s".formatted(i.getKey()), i.getValue().toString())));
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setTypeConverter(new StandardTypeConverter());
        return spELHelper.newGetterInstance(loadSpEL.get(), evaluationContext);
    }

    @Override
    protected <DATA, JOIN_DATA, JOIN_KEY> Function<JOIN_DATA, JOIN_KEY> createKeyFromJoinData(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        log.trace("JoinField-c[{}]-f[{}]: keyFromJoinData={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), annotation.keyFromJoinData());
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.keyFromJoinData()), "The keyFromJoinData on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        return spELHelper.newGetterInstance(annotation.keyFromJoinData());
    }

    @Override
    protected <DATA, JOIN_DATA> Function<JOIN_DATA, Boolean> createJoinDataFilter(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        if (StrUtil.isEmpty(annotation.joinDataFilter())) {
            log.trace("JoinField-c[{}]-f[{}]: joinDataFilter is empty", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field));
            return data -> Boolean.TRUE;
        }
        log.trace("JoinField-c[{}]-f[{}]: joinDataFilter={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), annotation.joinDataFilter());
        return spELHelper.newGetterInstance(annotation.joinDataFilter());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        if (StrUtil.isEmpty(annotation.joinDataConverter())) {
            log.trace("JoinField-c[{}]-f[{}]: joinDataConverter is empty", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field));
            return joinData -> (JOIN_RESULT) joinData;
        } else {
            log.trace("JoinField-c[{}]-f[{}]: joinDataConverter={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), annotation.joinDataConverter());
            return spELHelper.newGetterInstance(annotation.joinDataConverter());
        }
    }

    @Override
    protected <DATA, JOIN_RESULT> BiConsumer<DATA, Collection<JOIN_RESULT>> createFoundFunction(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        log.trace("JoinField-c[{}]-f[{}]: onFound Function is created", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field));
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setTypeConverter(new StandardTypeConverter());
        return spELHelper.newSetterInstance(field, evaluationContext);
    }


    /**
     * Generates a grouping key for the given join field based on its configuration.
     *
     * <p>This method creates a unique identifier string that represents the combination of:
     * <ul>
     *   <li>The annotation type name (用户自定义的JoinInMemory Wrapper注解的名称)</li>
     *   <li>Source data filter expression (前置过滤)</li>
     *   <li>Key extraction expression from join data (关联Key)</li>
     *   <li>Data loader expression (加载数据)</li>
     *   <li>Join data filter expression (后置过滤)</li>
     *   <li>Execution run level (Nessted Join存在时, 将使用计算后的实际RunLevel, 跟Nested层级有关)</li>
     *   <li>Non-meta annotation attributes (自定义的属性, 一般用于自定义传参)</li>
     * </ul>
     *
     * <p>The generated group name follows the format:
     * {@code AnnotationTypeName[sourceDataFilter+keyFromJoinData+loader+joinDataFilter+runLevel]attribute1=value1||attribute2=value2...}
     *
     * @param <DATA>     the type of data being processed
     * @param context    the join context containing processing information
     * @param field      the field being processed for joining
     * @param annotation the JoinInMemory annotation configuration
     * @return a function that generates group names from merged annotations
     */
    @Override
    public <DATA> Function<MergedAnnotation<?>, String> groupBy(JoinContext<DATA> context, Field field, JoinInMemory annotation) {
        return rootAnnotation -> {
            final String groupName = rootAnnotation.getType().getName() +
                    "[" +
                    annotation.sourceDataFilter() +
                    annotation.keyFromJoinData() +
                    annotation.loader() +
                    annotation.joinDataFilter() +
                    getActualRunLevel(context, annotation) +
                    "]" +
                    getNonMetaAnnotationAttributes(rootAnnotation)
                            .stream()
                            .map(e -> e.getKey() + "=" + e.getValue().toString())
                            .collect(Collectors.joining("||"));
            log.trace("JoinField-c[{}]-f[{}]: Grouping Name={}", context.getDataClass().getSimpleName(), context.getHierarchicalPath(field), groupName);
            return groupName;
        };
    }
}
