package com.luban.joininmemory.support;

import com.luban.joininmemory.JoinFieldExecutor;
import com.luban.joininmemory.JoinFieldExecutorFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @author hp 2023/3/27
 */
public abstract class AbstractAnnotationBasedJoinFieldExecutorFactory<A extends Annotation> implements JoinFieldExecutorFactory {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedJoinFieldExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <TYPE> List<JoinFieldExecutor<TYPE>> createForType(Class<TYPE> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        return fields.stream()
                .map(field -> buildJoinFieldExecutor(clazz, field, AnnotatedElementUtils.findMergedAnnotation(field, annotationClass)))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private <TYPE> JoinFieldExecutor<TYPE> buildJoinFieldExecutor(Class<TYPE> clazz, Field field, A annotation) {
        if (annotation == null) {
            return null;
        }
        return (DefaultJoinFieldExecutorAdaptor) DefaultJoinFieldExecutorAdaptor.builder()
                .name(createName(clazz, field, annotation))
                .runLevel(createRunLevel(clazz, field, annotation))
                .keyFromSource(createKeyGeneratorFromSourceData(clazz, field, annotation))
                .convertKeyFromSourceData(createKeyConverterFromSourceData(clazz, field, annotation))
                .keyFromJoinData(createKeyGeneratorFromJoinData(clazz, field, annotation))
                .convertKeyFromJoinData(createKeyConverterFromJoinData(clazz, field, annotation))
                .joinDataLoader(createDataLoader(clazz, field, annotation))
                .joinDataConverter(createDataConverter(clazz, field, annotation))
                .foundCallback(createFoundFunction(clazz, field, annotation))
                .lostCallback(createLostFunction(clazz, field, annotation))
                .build();
    }

    protected <DATA> BiConsumer<Object, Object> createLostFunction(Class<DATA> clazz, Field field, A annotation) {
        return null;
    }

    protected abstract <DATA> BiConsumer<Object, List<Object>> createFoundFunction(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createDataConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createKeyGeneratorFromJoinData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createKeyConverterFromJoinData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<List<Object>, List<Object>> createDataLoader(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createKeyGeneratorFromSourceData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createKeyConverterFromSourceData(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> int createRunLevel(Class<DATA> clazz, Field field, A annotation);

    protected <DATA> String createName(Class<DATA> clazz, Field field, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#field[" + field.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

}
