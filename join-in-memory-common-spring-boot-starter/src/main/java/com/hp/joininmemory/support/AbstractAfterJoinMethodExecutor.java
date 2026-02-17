package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Objects;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public abstract class AbstractAfterJoinMethodExecutor<DATA> implements AfterJoinMethodExecutor<DATA> {

    protected abstract void afterJoin(DATA data);

    protected abstract Collection<DATA> extractSourceData(DATA data);

    @Override
    public void execute(DATA data) {
        if (Objects.isNull(data)) {
            log.debug("AfterJoin Data is Null");
            return;
        }
        // nested join 支持, 要注意写扩散的问题, 不要做耗时的I/O任务
        final Collection<DATA> list = extractSourceData(data);
        log.debug("AfterJoin Extracted Data:{}", list);
        // 非 nested join 场景也就一个元素
        list.forEach(this::afterJoin);
    }

    @Override
    public void execute(DATA data, MeterRegistry meterRegistry) {
        final Timer.Sample sample = Timer.start(meterRegistry);
        try {
            execute(data);
        } finally {
            sample.stop(
                    Timer.builder("after.join.execution.time")
                            .tag("class", data.getClass().getSimpleName())
                            .register(meterRegistry)
            );
        }
    }
}
