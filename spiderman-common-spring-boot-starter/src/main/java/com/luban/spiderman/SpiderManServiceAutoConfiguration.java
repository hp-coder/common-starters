package com.luban.spiderman;

import com.luban.spiderman.factory.ConnectionFactory;
import com.luban.spiderman.factory.DomNodeExecutorFactory;
import com.luban.spiderman.factory.DomNodesExecutorFactory;
import com.luban.spiderman.factory.SpiderManService;
import com.luban.spiderman.factory.support.DefaultDomNodesExecutorFactory;
import com.luban.spiderman.factory.support.DefaultSpiderManServiceImpl;
import com.luban.spiderman.factory.support.SpiderConfigBasedConnectionFactory;
import com.luban.spiderman.factory.support.SpiderNodeBasedDomNodeExecutorFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hp
 */
@Configuration
public class SpiderManServiceAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DomNodesExecutorFactory domNodesExecutorFactory(
            Collection<? extends DomNodeExecutorFactory> domNodeExecutorFactories,
            Map<String, ExecutorService> executorServiceMap
    ) {
        return new DefaultDomNodesExecutorFactory(domNodeExecutorFactories, executorServiceMap);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpiderManService spiderManService(
            DomNodesExecutorFactory domNodesExecutorFactory,
            ConnectionFactory connectionFactory
    ) {
        return new DefaultSpiderManServiceImpl(domNodesExecutorFactory, connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory(
            ApplicationContext applicationContext
    ) {
        return new SpiderConfigBasedConnectionFactory(new BeanFactoryResolver(applicationContext));
    }

    @Bean
    @ConditionalOnMissingBean
    public SpiderNodeBasedDomNodeExecutorFactory spiderNodeBasedDomNodeExecutorFactory(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory,
            ApplicationContext applicationContext
    ) {
        return new SpiderNodeBasedDomNodeExecutorFactory(connectionFactory, new BeanFactoryResolver(applicationContext));
    }

    @Bean
    public ExecutorService defaultSpiderManExecutor() {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("SpiderManService-Thread-%d")
                .daemon(true)
                .build();
        int maxSize = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(0, maxSize,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                basicThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
