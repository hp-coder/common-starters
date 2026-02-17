package com.hp.joininmemory.support;

import com.google.common.collect.Lists;
import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.annotation.AfterJoin;
import com.hp.joininmemory.context.JoinContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.hp.joininmemory.support.JoinInMemoryBasedJoinFieldExecutorFactory.goDownThePath;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public class AfterJoinBasedAfterJoinMethodExecutorFactory extends AbstractAnnotationBasedAfterJoinMethodExecutorFactory<AfterJoin> {

    private final SpELHelper spELHelper;

    public AfterJoinBasedAfterJoinMethodExecutorFactory(SpELHelper spELHelper) {
        super(AfterJoin.class);
        this.spELHelper = spELHelper;
    }

    @Override
    protected <DATA> BiFunction<DATA, List<String>, Collection<DATA>> createExtractSourceData(JoinContext<DATA> context, Method method, AfterJoin annotation) {
        return (data, hierarchicalPaths) -> {
            final LinkedList<String> modifiablePaths = Lists.newLinkedList(hierarchicalPaths);
            return goDownThePath(spELHelper, modifiablePaths, Lists.newArrayList(data));
        };
    }

    @Override
    protected <DATA> Consumer<DATA> createForAfterJoin(JoinContext<DATA> context, Method method, AfterJoin afterJoin) {
        return data -> {
            try {
                MethodUtils.getAccessibleMethod(method).invoke(data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throwSimpleExceptionIfPossible(data, method, e);
            }
        };
    }

    @Override
    protected <DATA> int createForRunLevel(JoinContext<DATA> context, Method method, AfterJoin afterJoin) {
        log.trace("AfterJoinMethod-c[{}]-m[{}] runs on level {}", context.getDataClass().getSimpleName(), method.getName(), afterJoin.runLevel().getCode());
        return afterJoin.runLevel().getCode();
    }


    private void throwSimpleExceptionIfPossible(Object value, Method method, Throwable ex) {
        if (ex instanceof InvocationTargetException) {
            Throwable rootCause = ex.getCause();
            if (rootCause instanceof RuntimeException) {
                throw (RuntimeException) rootCause;
            }
            throw new RuntimeException("A problem occurred when trying to execute method '" + method.getName() +
                    "' on object of type [" + value.getClass().getName() + "]", rootCause);
        }
        throw new RuntimeException(ex);
    }
}
