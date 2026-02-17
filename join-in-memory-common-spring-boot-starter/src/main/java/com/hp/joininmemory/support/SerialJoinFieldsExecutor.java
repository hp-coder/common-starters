package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.utils.JoinHelper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public class SerialJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {

    public SerialJoinFieldsExecutor(
            Class<DATA> clazz,
            List<JoinFieldExecutor<DATA>> joinFieldExecutors,
            List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors
    ) {
        super(clazz, joinFieldExecutors, afterJoinMethodExecutors);
    }

    @Override
    public void execute(Collection<DATA> data) {
        execute(data, null);
    }

    @Override
    public void execute(Collection<DATA> data, MeterRegistry meterRegistry) {
        try {
            doExecute(data, meterRegistry);
        } finally {
            JoinHelper.clearDynamicFields();
        }
    }

    private void doExecute(Collection<DATA> dataList, MeterRegistry meterRegistry) {
        final List<JoinFieldExecutor<DATA>> executors = getJoinFieldExecutors()
                .stream()
                .filter(executor -> JoinHelper.isValidField(executor.getTargetClassName(), executor.getTargetFieldName()))
                .sorted(Comparator.comparing(JoinFieldExecutor::runOnLevel))
                .toList();

        executors.forEach(executor -> {
            if (log.isDebugEnabled()) {
                StopWatch stopwatch = new StopWatch("Start Executing Serial Join Tasks");
                stopwatch.start();
                Optional.ofNullable(meterRegistry).ifPresentOrElse(
                        mr -> executor.execute(dataList, mr),
                        () -> executor.execute(dataList)
                );
                stopwatch.stop();
                log.debug("Serial Join Execution Costs {} ms, The Join Executor is {}", stopwatch.getTotalTimeMillis(), executor);
            } else {
                Optional.ofNullable(meterRegistry).ifPresentOrElse(
                        mr -> executor.execute(dataList, mr),
                        () -> executor.execute(dataList)
                );
            }
        });

        final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors = getAfterJoinMethodExecutors()
                .stream()
                .sorted(Comparator.comparing(AfterJoinMethodExecutor::runOnLevel))
                .toList();

        if (log.isDebugEnabled()) {
            StopWatch stopwatch = new StopWatch("Starting executing after join tasks");
            stopwatch.start();
            dataList.forEach(data -> afterJoinMethodExecutors.forEach(e -> {
                Optional.ofNullable(meterRegistry).ifPresentOrElse(
                        mr -> e.execute(data, mr),
                        () -> e.execute(data)
                );
            }));
            stopwatch.stop();
            log.debug("Serial After Join Execution Costs {} ms", stopwatch.getTotalTimeMillis());
        } else {
            dataList.forEach(data -> afterJoinMethodExecutors.forEach(e -> {
                Optional.ofNullable(meterRegistry).ifPresentOrElse(
                        mr -> e.execute(data, mr),
                        () -> e.execute(data)
                );
            }));
        }
    }
}
