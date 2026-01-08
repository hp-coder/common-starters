package com.hp.joininmemory.cache;

import com.google.common.collect.Maps;
import com.hp.joininmemory.JoinFieldsExecutor;
import com.hp.joininmemory.endpoint.JoinInMemoryEndpoint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Join executor cache design
 * <p>
 * Metrics collection supported. See: {@link JoinInMemoryEndpoint}
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 * 
 */
public class JoinInMemoryCacheManager {

    private static final JoinInMemoryCacheManager INSTANCE = new JoinInMemoryCacheManager();

    private final Map<Class<?>, JoinFieldsExecutor<?>> cache = Maps.newConcurrentMap();

    public static JoinInMemoryCacheManager getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> JoinFieldsExecutor<T> getOrCreate(Class<T> clazz, Function<Class<T>, JoinFieldsExecutor<T>> creator) {
        return (JoinFieldsExecutor<T>) cache.computeIfAbsent(clazz, k -> creator.apply((Class<T>) k));
    }

    public long getCacheSize() {
        return cache.size();
    }

    public Set<Class<?>> getCacheKeys() {
        return new HashSet<>(cache.keySet());
    }

    public void clear() {
        cache.clear();
    }
}
