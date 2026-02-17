package com.hp.joininmemory.support;

import com.hp.joininmemory.JoinFieldsExecutor;
import com.hp.joininmemory.JoinFieldsExecutorFactory;
import com.hp.joininmemory.JoinInMemoryProperties;
import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.cache.JoinInMemoryCacheManager;
import com.hp.joininmemory.metrics.JoinInMemoryMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@RequiredArgsConstructor
public class DefaultJoinService implements JoinService {

    private final JoinFieldsExecutorFactory joinFieldsExecutorFactory;
    private final JoinInMemoryMetrics metrics;
    private final JoinInMemoryCacheManager cacheManager;
    private final JoinInMemoryProperties properties;

    @Override
    public <T> void joinInMemory(Class<T> klass, Collection<T> data) {
        final JoinFieldsExecutor<T> joinFieldsExecutor = cacheManager.getOrCreate(klass, joinFieldsExecutorFactory::createFor);

        if (!properties.getMetrics().isEnableMetrics()) {
            joinFieldsExecutor.execute(data);
            return;
        }

        metrics.recordJoinExecution(klass, data.size());
        final Timer.Sample sample = metrics.startExecutionTimer();
        try {
            joinFieldsExecutor.execute(data, metrics.getMeterRegistry());
        } finally {
            metrics.stopExecutionTimer(sample, klass);
        }
    }
}
