package com.hp.joininmemory.support;

import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldsExecutor;
import com.hp.joininmemory.JoinFieldsExecutorFactory;
import com.hp.joininmemory.JoinService;

import java.util.Collection;
import java.util.Map;

/**
 * @author hp 2023/3/27
 */
public class DefaultJoinService implements JoinService {

    private final JoinFieldsExecutorFactory joinFieldsExecutorFactory;

    public DefaultJoinService(JoinFieldsExecutorFactory joinFieldsExecutorFactory) {
        this.joinFieldsExecutorFactory = joinFieldsExecutorFactory;
    }

    @SuppressWarnings("rawtypes")
    private final Map<Class, JoinFieldsExecutor> cache = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T> void joinInMemory(Class<T> klass, Collection<T> data) {
        this.cache.computeIfAbsent(klass, this::createJoinExecutorGroup).execute(data);
    }

    @Override
    public <T> void register(Class<T> klass) {
        this.cache.computeIfAbsent(klass, this::createJoinExecutorGroup);
    }

    private <T> JoinFieldsExecutor<T> createJoinExecutorGroup(Class<T> klass) {
        return this.joinFieldsExecutorFactory.createFor(klass);
    }
}
