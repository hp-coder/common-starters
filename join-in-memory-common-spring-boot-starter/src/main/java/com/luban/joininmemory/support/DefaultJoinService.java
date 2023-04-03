package com.luban.joininmemory.support;

import com.google.common.collect.Maps;
import com.luban.joininmemory.JoinFieldsExecutor;
import com.luban.joininmemory.JoinFieldsExecutorFactory;
import com.luban.joininmemory.JoinService;

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

    @Override
    public <T> void joinInMemory(Class<T> clazz, List<T> data) {
        this.cache.computeIfAbsent(clazz, this::createJoinExecutorGroup).execute(data);
    }

    @Override
    public <T> void register(Class<T> tCls) {
        this.cache.computeIfAbsent(tCls, this::createJoinExecutorGroup);
    }

    private JoinFieldsExecutor createJoinExecutorGroup(Class aClass) {
        return this.joinFieldsExecutorFactory.createFor(aClass);
    }
}
