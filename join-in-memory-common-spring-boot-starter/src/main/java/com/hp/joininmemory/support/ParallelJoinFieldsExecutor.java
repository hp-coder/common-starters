package com.hp.joininmemory.support;

import com.alibaba.ttl.TtlCallable;
import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.exception.ExceptionNotifier;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import com.hp.joininmemory.utils.JoinHelper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
public class ParallelJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {

    private final ExecutorService executorService;
    private final List<JoinExecutorWithLevel<DATA>> joinExecutorWithLevels;
    private final List<AfterJoinExecutorWithLevel<DATA>> afterJoinExecutorWithLevels;
    private final ExceptionNotifier joinExceptionNotifier;
    private final ExceptionNotifier afterJoinExceptionNotifier;

    public ParallelJoinFieldsExecutor(Class<DATA> clazz,
                                      List<JoinFieldExecutor<DATA>> joinFieldExecutors,
                                      List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors,
                                      ExecutorService executorService,
                                      ExceptionNotifier joinExceptionNotifier,
                                      ExceptionNotifier afterJoinExceptionNotifier
    ) {
        super(clazz, joinFieldExecutors, afterJoinMethodExecutors);
        this.executorService = executorService;
        this.joinExecutorWithLevels = buildJoinExecutorWithLevel();
        this.afterJoinExecutorWithLevels = buildAfterJoinExecutorWithLevel();
        this.joinExceptionNotifier = joinExceptionNotifier;
        this.afterJoinExceptionNotifier = afterJoinExceptionNotifier;
    }

    private List<JoinExecutorWithLevel<DATA>> buildJoinExecutorWithLevel() {
        return getJoinFieldExecutors()
                .stream()
                .collect(Collectors.groupingBy(JoinFieldExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new JoinExecutorWithLevel<>(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(JoinExecutorWithLevel::level))
                .collect(Collectors.toList());
    }

    private List<AfterJoinExecutorWithLevel<DATA>> buildAfterJoinExecutorWithLevel() {
        return getAfterJoinMethodExecutors()
                .stream()
                .collect(Collectors.groupingBy(AfterJoinMethodExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new AfterJoinExecutorWithLevel<>(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(AfterJoinExecutorWithLevel::level))
                .collect(Collectors.toList());
    }

    @Override
    public void execute(Collection<DATA> data) {
        execute(data, null);
    }

    @Override
    public void execute(Collection<DATA> data, MeterRegistry meterRegistry) {
        try {
            log.debug("Executing Parallel Join Tasks");
            executeJoinTasks(data, meterRegistry);

            log.debug("Executing Parallel After Join Tasks");
            executeAfterJoinTasks(data, meterRegistry);
        } finally {
            log.debug("Prepare to Clean Dynamic Fields");
            JoinHelper.clearDynamicFields();
        }
    }

    private void executeJoinTasks(Collection<DATA> dataList, MeterRegistry meterRegistry) {
        this.joinExecutorWithLevels.forEach(leveledTasks -> {
            // Build tasks
            List<Callable<Void>> tasks = buildJoinTasks(leveledTasks, dataList, meterRegistry);
            // TTL capability
            tasks = ttlWrapper(tasks);

            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Start Executing Parallel Join Tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("Parallel Join Execution Costs {} ms", stopwatch.getTotalTimeMillis());
                } else {
                    this.executorService.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
            }
        });
    }

    private void executeAfterJoinTasks(Collection<DATA> dataList, MeterRegistry meterRegistry) {
        afterJoinExecutorWithLevels.forEach(leveledTasks -> {
            // Build tasks
            List<Callable<Void>> tasks = dataList.stream()
                    .flatMap(data -> buildAfterJoinTasks(leveledTasks, data, meterRegistry).stream())
                    .collect(Collectors.toList());
            // TTL capability
            tasks = ttlWrapper(tasks);

            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Start Executing Parallel After Join Tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("Parallel After Join Execution Costs {} ms", stopwatch.getTotalTimeMillis());
                } else {
                    this.executorService.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JoinException(JoinErrorCode.AFTER_JOIN_ERROR, e);
            }
        });
    }

    private List<Callable<Void>> ttlWrapper(List<Callable<Void>> tasks) {
        return tasks.stream()
                .map(TtlCallable::get)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Callable<Void>> buildJoinTasks(JoinExecutorWithLevel<DATA> leveledExecutors, Collection<DATA> dataList, MeterRegistry meterRegistry) {
        return leveledExecutors.joinFieldExecutors()
                .stream()
                .filter(executor -> JoinHelper.isValidField(executor.getTargetClassName(), executor.getTargetFieldName()))
                .map(executor -> new Task(data -> Optional.ofNullable(meterRegistry).ifPresentOrElse(
                        mr -> executor.execute((Collection<DATA>) data, mr),
                        () -> executor.execute((Collection<DATA>) data)
                ), dataList, joinExceptionNotifier))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Callable<Void>> buildAfterJoinTasks(AfterJoinExecutorWithLevel<DATA> leveledExecutors, DATA data, MeterRegistry meterRegistry) {
        return leveledExecutors.afterJoinMethodExecutors()
                .stream()
                .map(executor -> new Task(d -> {
                    Optional.ofNullable(meterRegistry).ifPresentOrElse(
                            mr -> executor.execute((DATA) d, mr),
                            () -> executor.execute((DATA) d)
                    );
                }, data, afterJoinExceptionNotifier))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    static class Task implements Callable<Void> {

        private final Consumer<Object> consumer;
        private final Object data;
        private final ExceptionNotifier exceptionNotifier;

        @Override
        public Void call() {
            try {
                consumer.accept(data);
            } catch (Exception e) {
                exceptionNotifier.handle().accept(data, e);
            }
            return null;
        }
    }

    record JoinExecutorWithLevel<DATA>(Integer level, List<JoinFieldExecutor<DATA>> joinFieldExecutors) {
    }

    record AfterJoinExecutorWithLevel<DATA>(Integer level,
                                            List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors) {
    }

}


