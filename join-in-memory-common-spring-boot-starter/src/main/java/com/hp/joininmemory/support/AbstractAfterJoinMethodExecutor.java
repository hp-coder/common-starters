package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Slf4j
public abstract class AbstractAfterJoinMethodExecutor<DATA_AFTER_JOIN> implements AfterJoinMethodExecutor<DATA_AFTER_JOIN> {

    protected abstract void afterJoin(DATA_AFTER_JOIN data);

    @Override
    public void execute(DATA_AFTER_JOIN data) {
        if (Objects.isNull(data)) {
            log.error("The data used in the after join stage is null.");
            return;
        }
        try {
            afterJoin(data);
        } catch (Exception e) {
            throw new JoinException(JoinErrorCode.AFTER_JOIN_ERROR, e);
        }
    }

    @Override
    public void execute(DATA_AFTER_JOIN data, MeterRegistry meterRegistry) {
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
