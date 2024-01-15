package com.hp.joininmemory.support;

import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldsExecutor;
import com.hp.joininmemory.JoinFieldsExecutorFactory;
import com.hp.joininmemory.JoinService;

import java.util.List;
import java.util.Map;

/**
 * @author hp 2023/3/27
 */
public class DefaultJoinService implements JoinService {

    private final JoinFieldsExecutorFactory joinFieldsExecutorFactory;

    public DefaultJoinService(JoinFieldsExecutorFactory joinFieldsExecutorFactory) {
        this.joinFieldsExecutorFactory = joinFieldsExecutorFactory;
    }

    private final Map<Class, JoinFieldsExecutor> cache = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T> void joinInMemory(Class<T> clazz, List<T> data) {
        this.cache.computeIfAbsent(clazz, this::createJoinExecutorGroup).execute(data);
    }

    @Override
    public <T> void register(Class<T> tCls) {
        this.cache.computeIfAbsent(tCls, this::createJoinExecutorGroup);
    }

    private <T> JoinFieldsExecutor<T> createJoinExecutorGroup(Class<T> aClass) {
        return this.joinFieldsExecutorFactory.createFor(aClass);
    }
}
