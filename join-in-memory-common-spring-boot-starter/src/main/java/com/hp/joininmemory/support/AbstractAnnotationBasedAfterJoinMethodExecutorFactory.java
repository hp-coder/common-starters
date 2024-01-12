package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.AfterJoinMethodExecutorFactory;
import com.hp.joininmemory.context.JoinContext;
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
    public <DATA> List<AfterJoinMethodExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final Class<DATA> clazz = context.getDataClass();
        final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, annotationClass);
        return methods.stream()
                .map(method -> buildAfterJoinMethodExecutor(clazz, method, AnnotatedElementUtils.findMergedAnnotation(method, annotationClass)))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private <DATA> AfterJoinMethodExecutor<DATA> buildAfterJoinMethodExecutor(Class<DATA> clazz, Method method, A annotation) {
        if (Objects.isNull(annotation)) {
            return null;
        }
        return new DefaultAfterJoinMethodExecutorAdaptor<>(
                createForName(clazz, method, annotation),
                createForRunLevel(clazz, method, annotation),
                createForAfterJoin(clazz, method, annotation)
        );
    }

    protected <DATA> String createForName(Class<DATA> clazz, Method method, A annotation) {
        return "class[" + clazz.getSimpleName() + "]" +
                "#method[" + method.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA> Consumer<DATA> createForAfterJoin(Class<DATA> clazz, Method method, A annotation);

    protected abstract <DATA> int createForRunLevel(Class<DATA> clazz, Method method, A annotation);
}
