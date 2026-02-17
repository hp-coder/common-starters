package com.hp.joininmemory.metrics;

import com.hp.joininmemory.cache.JoinInMemoryCacheManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/7
 */
@RequiredArgsConstructor
public class JoinInMemoryMetrics {

    @Getter
    private final MeterRegistry meterRegistry;

    // 执行计数器
    private final Counter joinExecutionCounter;

    public JoinInMemoryMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // 执行计数器
        this.joinExecutionCounter = Counter.builder("join.executions")
                .description("Total number of join executions")
                .register(meterRegistry);
    }

    public void recordJoinExecution(Class<?> clazz, int dataSize) {
        joinExecutionCounter.increment(dataSize);
    }

    public Timer.Sample startExecutionTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopExecutionTimer(Timer.Sample sample, Class<?> clazz) {
        sample.stop(
                Timer.builder("join.execution.time")
                        .tag("class", clazz.getSimpleName())
                        .register(meterRegistry)
        );
    }

    public long getCacheSize() {
        return JoinInMemoryCacheManager.getInstance().getCacheSize();
    }
}
