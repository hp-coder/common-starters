package com.hp.joininmemory.support;

import com.alibaba.ttl.TtlCallable;
import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.exception.ExceptionNotifier;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import com.hp.joininmemory.utils.JoinHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
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
    public void execute(Collection<DATA> dataList) {
        try {
            executeJoinTasks(dataList);
        } finally {
            JoinHelper.clearDynamicFields();
        }
        executeAfterJoinTasks(dataList);
    }

    private void executeJoinTasks(Collection<DATA> dataList) {
        this.joinExecutorWithLevels.forEach(leveledTasks -> {
            log.debug("run join on level {} use {}", leveledTasks.level(), leveledTasks.joinFieldExecutors());
            List<Callable<Void>> tasks = buildJoinTasks(leveledTasks, dataList);
            tasks = ttlWrapper(tasks);
            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Starting executing join tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("run execute cost {} ms", stopwatch.getTotalTimeMillis());
                } else {
                    this.executorService.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
            }
        });
    }

    private void executeAfterJoinTasks(Collection<DATA> dataList) {
        afterJoinExecutorWithLevels.forEach(leveled -> {
            List<Callable<Void>> tasks = dataList.stream()
                    .flatMap(data -> buildAfterJoinTasks(leveled, data).stream())
                    .collect(Collectors.toList());
            tasks = ttlWrapper(tasks);
            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Starting executing after join tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("run execute cost {} ms.", stopwatch.getTotalTimeMillis());
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
    private List<Callable<Void>> buildJoinTasks(JoinExecutorWithLevel<DATA> leveledExecutors, Collection<DATA> dataList) {
        return leveledExecutors.joinFieldExecutors()
                .stream()
                .filter(executor -> JoinHelper.notExcluded(executor.getTargetClassName(), executor.getTargetFieldName()) &&
                        JoinHelper.isIncluded(executor.getTargetClassName(), executor.getTargetFieldName()))
                .map(executor -> new Task(data -> executor.execute((Collection<DATA>) data), dataList, joinExceptionNotifier))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Callable<Void>> buildAfterJoinTasks(AfterJoinExecutorWithLevel<DATA> leveledExecutors, DATA data) {
        return leveledExecutors.afterJoinMethodExecutors()
                .stream()
                .map(executor -> new Task(d -> executor.execute((DATA) d), data, afterJoinExceptionNotifier))
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


