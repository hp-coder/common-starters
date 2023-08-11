package com.luban.spiderman.factory.support;

import com.google.common.collect.Maps;
import com.luban.spiderman.factory.ConnectionFactory;
import com.luban.spiderman.factory.DomNodesExecutor;
import com.luban.spiderman.factory.DomNodesExecutorFactory;
import com.luban.spiderman.factory.SpiderManService;
import org.jsoup.Connection;

import java.util.List;
import java.util.Map;

/**
 * @author hp
 */
public class DefaultSpiderManServiceImpl implements SpiderManService {

    private final DomNodesExecutorFactory domNodesExecutorFactory;
    private final ConnectionFactory connectionFactory;

    public DefaultSpiderManServiceImpl(DomNodesExecutorFactory domNodesExecutorFactory, ConnectionFactory connectionFactory) {
        this.domNodesExecutorFactory = domNodesExecutorFactory;
        this.connectionFactory = connectionFactory;
    }

    private final Map<Class, DomNodesExecutor> cache = Maps.newConcurrentMap();

    @Override
    public <T> List<T> spider(Class<T> clazz, List<Connection> connections) {
        return this.cache.computeIfAbsent(clazz, this::createDomNodeExecutorGroup).execute(connections);
    }

    @Override
    public <T> List<T> spider(Class<T> clazz) {
        return this.cache.computeIfAbsent(clazz, this::createDomNodeExecutorGroup).execute(connectionFactory.createFor(clazz));
    }

    @Override
    public <T> void register(Class<T> tCls) {
        this.cache.computeIfAbsent(tCls, this::createDomNodeExecutorGroup);
    }

    private DomNodesExecutor createDomNodeExecutorGroup(Class aClass) {
        return this.domNodesExecutorFactory.createFor(aClass);
    }
}
