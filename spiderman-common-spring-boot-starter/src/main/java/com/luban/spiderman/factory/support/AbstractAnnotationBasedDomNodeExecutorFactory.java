package com.luban.spiderman.factory.support;

import com.luban.spiderman.factory.DomNodeExecutor;
import com.luban.spiderman.factory.DomNodeExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractAnnotationBasedDomNodeExecutorFactory<A extends Annotation> implements DomNodeExecutorFactory {

    public final Class<A> annotationClass;

    public AbstractAnnotationBasedDomNodeExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <TYPE> List<DomNodeExecutor<TYPE>> createForType(Class<TYPE> clazz) {
        final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        return fields
                .stream()
                .map(field ->
                        (DomNodeExecutor<TYPE>) this.buildSpiderNodeExecutor(clazz, field, AnnotatedElementUtils.findMergedAnnotation(field, annotationClass))
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <TYPE> DomNodeExecutor buildSpiderNodeExecutor(Class<TYPE> clazz, Field field, A annotation) {
        if (annotation == null) {
            return null;
        }
        return DefaultDomNodeExecutorAdapter.builder()
                .name(createName(clazz, field, annotation))
                .runLevel(createRunLevel(clazz, field, annotation))
                .retrieveNode(createNodeFilters(clazz, field, annotation))
                .retrieveDataByNode(createDataRetriever(clazz, field, annotation))
                .convertToResult(createDataConverter(clazz, field, annotation))
                .beforeOnFound(createBeforeOnFound(clazz, field, annotation))
                .onFound(createOnFound(clazz, field, annotation))
                .build();
    }

    protected abstract <DATA> Function<Document, List<Object>> createNodeFilters(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createDataRetriever(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createDataConverter(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> Function<Object, Object> createBeforeOnFound(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> BiConsumer<Object, List<Object>> createOnFound(Class<DATA> clazz, Field field, A annotation);

    protected abstract <DATA> int createRunLevel(Class<DATA> clazz, Field field, A annotation);

    protected <DATA> String createName(Class<DATA> clazz, Field field, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#field[" + field.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

}
