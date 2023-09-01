package com.luban.joininmemory.support;

import com.luban.joininmemory.annotation.AfterJoin;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * @author hp
 */
public class AfterJoinBasedAfterJoinMethodExecutorFactory extends AbstractAnnotationBasedAfterJoinMethodExecutorFactory<AfterJoin> {
    public AfterJoinBasedAfterJoinMethodExecutorFactory() {
        super(AfterJoin.class);
    }

    @Override
    protected <DATA_AFTER_JOIN> Consumer<Object> createForAfterJoin(Class<DATA_AFTER_JOIN> clazz, Method method, AfterJoin afterJoin) {
        return dataAfterJoin -> {
            try {
                MethodUtils.getAccessibleMethod(method).invoke(dataAfterJoin);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected <DATA_AFTER_JOIN> int createForRunOrder(Class<DATA_AFTER_JOIN> clazz, Method method, AfterJoin afterJoin) {
        return afterJoin.order();
    }
}
