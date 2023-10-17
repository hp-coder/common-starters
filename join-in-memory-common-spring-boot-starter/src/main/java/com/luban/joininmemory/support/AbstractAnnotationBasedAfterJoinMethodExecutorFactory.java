package com.luban.joininmemory.support;

import com.luban.joininmemory.AfterJoinMethodExecutor;
import com.luban.joininmemory.AfterJoinMethodExecutorFactory;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * @author hp
 */
public abstract class AbstractAnnotationBasedAfterJoinMethodExecutorFactory<A extends Annotation> implements AfterJoinMethodExecutorFactory {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedAfterJoinMethodExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA_AFTER_JOIN> List<AfterJoinMethodExecutor<DATA_AFTER_JOIN>> createForType(Class<DATA_AFTER_JOIN> clazz) {
        final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, annotationClass);
        return methods.stream()
                .map(method -> buildAfterJoinMethodExecutor(clazz, method, AnnotatedElementUtils.findMergedAnnotation(method, annotationClass)))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private <DATA_AFTER_JOIN> AfterJoinMethodExecutor<DATA_AFTER_JOIN> buildAfterJoinMethodExecutor(Class<DATA_AFTER_JOIN> clazz, Method method, A annotation) {
        return (AfterJoinMethodExecutor<DATA_AFTER_JOIN>)
                DefaultAfterJoinMethodExecutorAdaptor
                        .builder()
                        .name(createForName(clazz, method, annotation))
                        .runLevel(createForRunLevel(clazz, method, annotation))
                        .afterjoin(createForAfterJoin(clazz, method, annotation))
                        .build();
    }

    protected <DATA> String createForName(Class<DATA> clazz, Method method, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#method[" + method.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA_AFTER_JOIN> Consumer<Object> createForAfterJoin(Class<DATA_AFTER_JOIN> clazz, Method method, A annotation);

    protected abstract <DATA_AFTER_JOIN> int createForRunLevel(Class<DATA_AFTER_JOIN> clazz, Method method, A annotation);
}
