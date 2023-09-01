package com.luban.joininmemory;

import com.luban.joininmemory.support.AfterJoinBasedAfterJoinMethodExecutorFactory;
import com.luban.joininmemory.support.DefaultJoinFieldsExecutorFactory;
import com.luban.joininmemory.support.DefaultJoinService;
import com.luban.joininmemory.support.JoinInMemoryBasedJoinFieldExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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
 * @author hp 2023/3/27
 */
@Slf4j
@Configuration
public class JoinInMemoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JoinFieldsExecutorFactory joinFieldsExecutorFactory(
            Collection<? extends JoinFieldExecutorFactory> joinFieldExecutorFactories,
            Collection<? extends AfterJoinMethodExecutorFactory> afterJoinMethodExecutorFactories,
            Map<String, ExecutorService> executorServiceMap
    ) {
        return new DefaultJoinFieldsExecutorFactory(
                joinFieldExecutorFactories,
                afterJoinMethodExecutorFactories,
                executorServiceMap
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public JoinService joinService(JoinFieldsExecutorFactory joinFieldsExecutorFactory) {
        return new DefaultJoinService(joinFieldsExecutorFactory);
    }

    @Bean
    public JoinInMemoryBasedJoinFieldExecutorFactory joinInMemoryBasedJoinItemExecutorFactory(ApplicationContext applicationContext) {
        return new JoinInMemoryBasedJoinFieldExecutorFactory(new BeanFactoryResolver(applicationContext));
    }

    @Bean
    public AfterJoinBasedAfterJoinMethodExecutorFactory afterJoinBasedAfterJoinMethodExecutorFactory() {
        return new AfterJoinBasedAfterJoinMethodExecutorFactory();
    }

    @Bean
    public ExecutorService defaultJoinInMemoryExecutor() {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("JoinInMemory-Thread-%d")
                .daemon(true)
                .build();
        int maxSize = Runtime.getRuntime().availableProcessors() * 3;
        return new ThreadPoolExecutor(0, maxSize,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                basicThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
