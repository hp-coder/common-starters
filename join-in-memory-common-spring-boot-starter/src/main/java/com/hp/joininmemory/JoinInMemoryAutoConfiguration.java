package com.hp.joininmemory;


import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.aspect.JoinAtReturnAdvice;
import com.hp.joininmemory.cache.JoinInMemoryCacheManager;
import com.hp.joininmemory.endpoint.JoinInMemoryEndpoint;
import com.hp.joininmemory.exception.AfterJoinExceptionNotifier;
import com.hp.joininmemory.exception.JoinExceptionNotifier;
import com.hp.joininmemory.metrics.JoinInMemoryMetrics;
import com.hp.joininmemory.support.AfterJoinBasedAfterJoinMethodExecutorFactory;
import com.hp.joininmemory.support.DefaultJoinFieldsExecutorFactory;
import com.hp.joininmemory.support.DefaultJoinService;
import com.hp.joininmemory.support.JoinInMemoryBasedJoinFieldExecutorFactory;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Slf4j
@Configuration
@Import(JoinInMemoryProperties.class)
public class JoinInMemoryAutoConfiguration {

    @Bean
    @ConditionalOnClass(value = {org.aspectj.lang.annotation.Aspect.class})
    public JoinAtReturnAdvice joinAtReturnAdvice(
            @Qualifier("joinService") JoinService joinService,
            SpELHelper spELHelper
    ) {
        return new JoinAtReturnAdvice(joinService, spELHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinFieldsExecutorFactory joinFieldsExecutorFactory(
            Collection<? extends JoinFieldExecutorFactory> joinFieldExecutorFactories,
            Collection<? extends AfterJoinMethodExecutorFactory> afterJoinMethodExecutorFactories,
            Map<String, ExecutorService> executorServiceMap,
            JoinExceptionNotifier joinExceptionNotifier,
            AfterJoinExceptionNotifier afterJoinExceptionNotifier,
            MeterRegistry meterRegistry
    ) {
        return new DefaultJoinFieldsExecutorFactory(
                joinFieldExecutorFactories,
                afterJoinMethodExecutorFactories,
                executorServiceMap,
                joinExceptionNotifier,
                afterJoinExceptionNotifier,
                meterRegistry
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinExceptionNotifier joinExceptionNotifier() {
        return () -> (data, ex) -> {
            log.error("Join Exception:", ex);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public AfterJoinExceptionNotifier afterJoinExceptionNotifier() {
        return () -> (data, ex) -> {
            log.error("AfterJoin Exception:", ex);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinInMemoryEndpoint joinInMemoryEndpoint(JoinInMemoryCacheManager cacheManager) {
        return new JoinInMemoryEndpoint(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinInMemoryMetrics joinInMemoryMetrics(MeterRegistry meterRegistry) {
        return new JoinInMemoryMetrics(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinInMemoryCacheManager joinInMemoryCacheManager() {
        return JoinInMemoryCacheManager.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinService joinService(
            JoinFieldsExecutorFactory joinFieldsExecutorFactory,
            JoinInMemoryMetrics joinInMemoryMetrics,
            JoinInMemoryCacheManager joinInMemoryCacheManager,
            JoinInMemoryProperties joinInMemoryProperties
    ) {
        return new DefaultJoinService(joinFieldsExecutorFactory, joinInMemoryMetrics, joinInMemoryCacheManager, joinInMemoryProperties);
    }

    @Bean
    public JoinInMemoryBasedJoinFieldExecutorFactory joinInMemoryBasedJoinItemExecutorFactory(SpELHelper spELHelper) {
        return new JoinInMemoryBasedJoinFieldExecutorFactory(spELHelper);
    }

    @Bean
    public AfterJoinBasedAfterJoinMethodExecutorFactory afterJoinBasedAfterJoinMethodExecutorFactory() {
        return new AfterJoinBasedAfterJoinMethodExecutorFactory();
    }

    @Bean
    public ExecutorService defaultJoinInMemoryExecutor() {
        final BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("JoinInMemory-Thread-%d")
                .daemon(true)
                .build();
        final int maxSize = Runtime.getRuntime().availableProcessors() * 3;
        return new ThreadPoolExecutor(
                0,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                basicThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
