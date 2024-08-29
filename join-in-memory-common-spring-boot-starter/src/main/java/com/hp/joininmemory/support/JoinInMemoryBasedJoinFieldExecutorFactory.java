package com.hp.joininmemory.support;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.annotation.JoinInMemory;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class JoinInMemoryBasedJoinFieldExecutorFactory extends AbstractAnnotationBasedJoinFieldExecutorFactory<JoinInMemory> {
    private final SpELHelper spELHelper;

    public JoinInMemoryBasedJoinFieldExecutorFactory(SpELHelper spELHelper) {
        super(JoinInMemory.class);
        this.spELHelper = spELHelper;
    }

    @Override
    protected <DATA> int createRunLevel(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.trace("run level for class {} field {}, is {}", clazz, field.getName(), annotation.runLevel());
        return annotation.runLevel().getCode();
    }

    @Override
    protected <DATA> Function<DATA, Boolean> createSourceDataFilter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        final String expression = annotation.sourceDataFilter();
        if (StrUtil.isEmpty(expression)) {
            return data -> Boolean.TRUE;
        }
        return spELHelper.newGetterInstance(expression);
    }

    @Override
    protected <DATA, JOIN_KEY> Function<DATA, JOIN_KEY> createKeyFromSourceData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.trace("Key from source data for class {} field {}, is {}", clazz, field.getName(), annotation.keyFromJoinData());
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.keyFromSourceData()), "The keyFromSourceData on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        return spELHelper.newGetterInstance(annotation.keyFromSourceData());
    }

    @Override
    protected <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.loader()), "The loader on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        final AtomicReference<String> loadSpEL = new AtomicReference<>(annotation.loader());
        log.trace("data loader for class {}  field {}, is {}", clazz, field.getName(), loadSpEL);

        getNonMetaAnnotationAttributes(getRootAnnotation(field)).forEach(i -> loadSpEL.set(loadSpEL.get().replace("#%s".formatted(i.getKey()), i.getValue().toString())));
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setTypeConverter(new StandardTypeConverter());
        return spELHelper.newGetterInstance(loadSpEL.get(), evaluationContext);
    }

    @Override
    protected <DATA, JOIN_DATA, JOIN_KEY> Function<JOIN_DATA, JOIN_KEY> createKeyFromJoinData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.trace("The key from join data is {}, the class is {}, the field is {}", annotation.keyFromJoinData(), clazz, field.getName());
        Preconditions.checkArgument(StrUtil.isNotEmpty(annotation.keyFromJoinData()), "The keyFromJoinData on the %s can not be empty.".formatted(getRootAnnotation(field).getType().getName()));
        return spELHelper.newGetterInstance(annotation.keyFromJoinData());
    }

    @Nullable
    @Override
    protected <DATA, JOIN_DATA> Function<JOIN_DATA, Boolean> createJoinDataFilter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        final String expression = annotation.joinDataFilter();
        if (StrUtil.isEmpty(expression)) {
            return data -> Boolean.TRUE;
        }
        return spELHelper.newGetterInstance(expression);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    protected <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StrUtil.isEmpty(annotation.joinDataConverter())) {
            log.trace("No data convert for class {}, field {}", clazz, field.getName());
            return joinData -> (JOIN_RESULT) joinData;
        } else {
            log.trace("The data-converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return spELHelper.newGetterInstance(annotation.joinDataConverter());
        }
    }

    @Override
    protected <DATA, JOIN_RESULT> BiConsumer<DATA, Collection<JOIN_RESULT>> createFoundFunction(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.trace("the field about to be set is {}, a type of {}", field.getName(), clazz);
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setTypeConverter(new StandardTypeConverter());
        return spELHelper.newSetterInstance(field, evaluationContext);
    }

    @Override
    public <DATA> Function<MergedAnnotation<?>, String> groupBy(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        return rootAnnotation -> {
            final String groupName = rootAnnotation.getType().getName() +
                    "[" +
                    annotation.sourceDataFilter() +
                    annotation.keyFromJoinData() +
                    annotation.loader() +
                    annotation.joinDataFilter() +
                    annotation.runLevel() +
                    "]" +
                    getNonMetaAnnotationAttributes(rootAnnotation)
                            .stream()
                            .map(e -> e.getKey() + "=" + e.getValue().toString())
                            .collect(Collectors.joining("||"));
            log.trace("Creating Join groupName={}", groupName);
            return groupName;
        };
    }
}
