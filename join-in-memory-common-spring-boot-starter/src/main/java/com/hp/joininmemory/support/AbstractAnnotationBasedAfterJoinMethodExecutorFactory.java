package com.hp.joininmemory.support;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.AfterJoinMethodExecutorFactory;
import com.hp.joininmemory.annotation.NestedJoin;
import com.hp.joininmemory.context.JoinContext;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public abstract class AbstractAnnotationBasedAfterJoinMethodExecutorFactory<A extends Annotation> implements AfterJoinMethodExecutorFactory {

    public final Class<A> annotationClass;

    protected AbstractAnnotationBasedAfterJoinMethodExecutorFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public <DATA> List<AfterJoinMethodExecutor<DATA>> createForType(JoinContext<DATA> context) {
        final List<AfterJoinMethodExecutor<DATA>> holder = Lists.newArrayList();
        createNestedAfterJoinFieldExecutor(context, holder);
        return holder;
    }

    private <DATA> void createNestedAfterJoinFieldExecutor(JoinContext<DATA> context, List<AfterJoinMethodExecutor<DATA>> holder) {
        final Class<?>[] clazz = {context.getCurrentClass()};
        final List<Field> fields = context.getCurrentClassFields();
        fields.forEach(field -> {
            if (AnnotatedElementUtils.isAnnotated(field, NestedJoin.class)) {
                if (JoinContext.invalidFieldType(clazz, field)) {
                    return;
                }
                context.enter(field, clazz[0]);
                createNestedAfterJoinFieldExecutor(context, holder);
            }
        });

        // 递归从这里出来. 所以 clazz 并不会被更新
        final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(context.getCurrentClass(), annotationClass);
        if (CollUtil.isNotEmpty(methods)) {
            final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors = methods.stream()
                    .map(method -> buildAfterJoinMethodExecutor(context, method, AnnotatedElementUtils.getMergedAnnotation(method, annotationClass)))
                    .filter(Objects::nonNull)
                    .toList();
            holder.addAll(afterJoinMethodExecutors);
        }
        context.exit();
    }

    private <DATA> AfterJoinMethodExecutor<DATA> buildAfterJoinMethodExecutor(JoinContext<DATA> context, Method method, A annotation) {
        if (Objects.isNull(annotation)) {
            return null;
        }
        return new DefaultAfterJoinMethodExecutorAdaptor<>(
                createForName(context, method, annotation),
                createForRunLevel(context, method, annotation),
                context.getHierarchicalPaths(),
                createExtractSourceData(context, method, annotation),
                createForAfterJoin(context, method, annotation)
        );
    }

    protected abstract <DATA> BiFunction<DATA, List<String>, Collection<DATA>> createExtractSourceData(JoinContext<DATA> context, Method method, A annotation);

    protected <DATA> String createForName(JoinContext<DATA> context, Method method, A annotation) {
        return "class[" + context.getDataClass().getSimpleName() + "]" +
                "#method[" + method.getName() + "]" +
                "-" + annotation.getClass().getSimpleName();
    }

    protected abstract <DATA> Consumer<DATA> createForAfterJoin(JoinContext<DATA> context, Method method, A annotation);

    protected abstract <DATA> int createForRunLevel(JoinContext<DATA> context, Method method, A annotation);
}
