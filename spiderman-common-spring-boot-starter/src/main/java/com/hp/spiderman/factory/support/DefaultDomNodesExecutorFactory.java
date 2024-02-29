package com.hp.spiderman.factory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.spiderman.annotations.SpiderConfig;
import com.hp.spiderman.constants.SpiderManExecutorType;
import com.hp.spiderman.factory.DomNodeExecutor;
import com.hp.spiderman.factory.DomNodeExecutorFactory;
import com.hp.spiderman.factory.DomNodesExecutor;
import com.hp.spiderman.factory.DomNodesExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public class DefaultDomNodesExecutorFactory implements DomNodesExecutorFactory {

    private final List<DomNodeExecutorFactory> spiderNodeExecutorFactories;
    private final Map<String, ExecutorService> executorServiceMap;


    public DefaultDomNodesExecutorFactory(
            Collection<? extends DomNodeExecutorFactory> spiderNodeExecutorFactories,
            Map<String, ExecutorService> executorServiceMap) {
        this.spiderNodeExecutorFactories = Lists.newArrayList(spiderNodeExecutorFactories);
        AnnotationAwareOrderComparator.sort(this.spiderNodeExecutorFactories);
        this.executorServiceMap = executorServiceMap;
    }

    @Override
    public <DATA> DomNodesExecutor<DATA> createFor(Class<DATA> clazz) {
        final SpiderConfig spiderConfig = clazz.getAnnotation(SpiderConfig.class);
        final List<DomNodeExecutor<DATA>> spiderNodeExecutors = this.spiderNodeExecutorFactories
                .stream()
                .flatMap(factory -> factory.createForType(clazz).stream())
                .collect(Collectors.toList());
        return buildSpiderNodesExecutor(clazz, spiderNodeExecutors, spiderConfig);
    }

    private <DATA> DomNodesExecutor<DATA> buildSpiderNodesExecutor(Class<DATA> clazz, List<DomNodeExecutor<DATA>> spiderNodeExecutors, SpiderConfig spiderConfig) {
        if (spiderConfig == null || spiderConfig.executorType() == SpiderManExecutorType.SINGLE) {
            log.info("Spider Man for {} uses parallel serial executor", clazz);
            return new SingleWorldDomNodesExecutor<>(clazz, spiderNodeExecutors);
        }
        if (spiderConfig.executorType() == SpiderManExecutorType.MULTIVERSE) {
            log.info("Spider Man for {} uses parallel executor, the executor pool is {}", clazz, spiderConfig.executorName());
            ExecutorService executor = executorServiceMap.get(spiderConfig.executorName());
            Preconditions.checkArgument(executor != null);
            return new MultiverseDomNodesExecutor<>(clazz, spiderNodeExecutors, executor);
        }
        throw new IllegalArgumentException("无效类型");
    }
}
