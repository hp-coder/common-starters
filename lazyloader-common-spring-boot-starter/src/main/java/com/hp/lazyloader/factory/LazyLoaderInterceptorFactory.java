package com.hp.lazyloader.factory;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.hp.lazyloader.LazyLoaderInterceptor;
import com.hp.lazyloader.PropertyLazyLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class LazyLoaderInterceptorFactory {

    private final Map<Class<?>, Map<String, PropertyLazyLoader>> lazyLoaderCache = Maps.newConcurrentMap();

    private final LazyLoaderFactory lazyLoaderFactory;

    public LazyLoaderInterceptorFactory(LazyLoaderFactory lazyLoaderFactory) {
        this.lazyLoaderFactory = lazyLoaderFactory;
    }

    public LazyLoaderInterceptor createFor(Class<?> clazz, Object instance) {
        final Map<String, PropertyLazyLoader> perClassCache = this.lazyLoaderCache.computeIfAbsent(clazz, this::createPerClassLazyLoadCache);
        return new LazyLoaderInterceptor(perClassCache, instance);
    }

    private Map<String, PropertyLazyLoader> createPerClassLazyLoadCache(Class<?> klass) {
        final List<PropertyLazyLoader> lazyLoaders = lazyLoaderFactory.createFor(klass);
        if (CollUtil.isEmpty(lazyLoaders)) {
            return Collections.emptyMap();
        }
        return lazyLoaders.stream()
                .collect(Collectors.toMap(f -> f.getField().getName(), Function.identity()));
    }
}
