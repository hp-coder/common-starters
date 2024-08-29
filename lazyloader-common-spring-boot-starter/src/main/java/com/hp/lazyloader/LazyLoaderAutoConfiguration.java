package com.hp.lazyloader;

import com.hp.common.base.utils.SpELHelper;
import com.hp.lazyloader.factory.AbstractLazyLoaderProxyFactory;
import com.hp.lazyloader.factory.LazyLoaderFactory;
import com.hp.lazyloader.factory.LazyLoaderInterceptorFactory;
import com.hp.lazyloader.factory.LazyLoaderProxyFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hp
 */
@Configuration
public class LazyLoaderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LazyLoaderFactory.class)
    @ConditionalOnBean(SpELHelper.class)
    public LazyLoaderFactory lazyLoaderFactory(SpELHelper spELHelper) {
        return new LazyLoaderFactory(spELHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LazyLoaderInterceptorFactory lazyLoaderInterceptorFactory(LazyLoaderFactory lazyLoaderFactory) {
        return new LazyLoaderInterceptorFactory(lazyLoaderFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public LazyLoaderProxyFactory lazyLoaderProxyFactory(LazyLoaderInterceptorFactory lazyLoaderInterceptorFactory) {
        return new AbstractLazyLoaderProxyFactory(lazyLoaderInterceptorFactory) {
        };
    }
}
