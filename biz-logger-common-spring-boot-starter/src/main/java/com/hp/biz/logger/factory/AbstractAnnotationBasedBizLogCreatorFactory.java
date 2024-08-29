
package com.hp.biz.logger.factory;

import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizLogOperator;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.EvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * @author hp
 */
public abstract class AbstractAnnotationBasedBizLogCreatorFactory<A extends Annotation> implements IBizLogCreatorFactory {

    protected final Class<A> annotationClass;

    public AbstractAnnotationBasedBizLogCreatorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public List<IBizLogCreator> createFor(Class<?> targetClass, Method method) {
        return createBizLoggerExecutors(targetClass, method);
    }

    protected List<IBizLogCreator> createBizLoggerExecutors(Class<?> targetClass, Method method) {
        if (!AnnotatedElementUtils.isAnnotated(method, annotationClass)) {
            return Collections.emptyList();
        }
        final Set<A> annotations = AnnotatedElementUtils.findMergedRepeatableAnnotations(method, annotationClass);
        return annotations.stream()
                .map(annotation -> createBizLoggerExecutor(targetClass, method, annotation))
                .toList();
    }

    protected IBizLogCreator createBizLoggerExecutor(Class<?> targetClass, Method method, A annotation) {
        return new DefaultBizLogCreator(
                createForOrder(targetClass, method, annotation),
                createForPreInvocation(targetClass, method, annotation),
                createForCondition(targetClass, method, annotation),
                createForTitle(targetClass, method, annotation),
                createForBizNo(targetClass, method, annotation),
                createForType(targetClass, method, annotation),
                createForSubType(targetClass, method, annotation),
                createForScope(targetClass, method, annotation),
                createForOperator(targetClass, method, annotation),
                createForSuccessLog(targetClass, method, annotation),
                createForErrorLog(targetClass, method, annotation),
                createForDiffs(targetClass, method, annotation)
        );
    }

    protected String stringify(Object object) {
        return Optional.ofNullable(object).map(Objects::toString).orElse(null);
    }

    protected abstract int createForOrder(Class<?> targetClass, Method method, A annotation);

    protected abstract boolean createForPreInvocation(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForTitle(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForBizNo(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForType(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForSubType(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForScope(Class<?> targetClass, Method method, A annotation);

    protected abstract BizLogOperator createForOperator(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForSuccessLog(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, String> createForErrorLog(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, Boolean> createForCondition(Class<?> targetClass, Method method, A annotation);

    protected abstract Function<EvaluationContext, List<BizDiffDTO>> createForDiffs(Class<?> targetClass, Method method, A annotation);
}
