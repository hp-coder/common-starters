package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.exception.ExceptionNotifier;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
 * @author hp 2023/3/27
 */
@Slf4j
public class ParallelJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {

    private final ExecutorService executorService;
    private final List<JoinExecutorWithLevel> joinExecutorWithLevels;
    private final List<AfterJoinExecutorWithLevel> afterJoinExecutorWithLevels;
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

    private List<JoinExecutorWithLevel> buildJoinExecutorWithLevel() {
        return getJoinFieldExecutors()
                .stream()
                .collect(Collectors.groupingBy(JoinFieldExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new JoinExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(i -> i.level))
                .collect(Collectors.toList());
    }

    private List<AfterJoinExecutorWithLevel> buildAfterJoinExecutorWithLevel() {
        return getAfterJoinMethodExecutors()
                .stream()
                .collect(Collectors.groupingBy(AfterJoinMethodExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new AfterJoinExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(i -> i.level))
                .collect(Collectors.toList());
    }

    @Override
    public void execute(Collection<DATA> dataList) {
        executeJoinTasks(dataList);
        executeAfterJoinTasks(dataList);
    }

    private void executeJoinTasks(Collection<DATA> dataList) {
        this.joinExecutorWithLevels.forEach(leveledTasks -> {
            log.debug("run join on level {} use {}", leveledTasks.getLevel(), leveledTasks.getJoinFieldExecutors());
            final List<Task> tasks = buildJoinTasks(leveledTasks, dataList);
            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Starting executing join tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("run execute cost {} ms, task is {}.", stopwatch.getTotalTimeMillis(), tasks);
                } else {
                    this.executorService.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
            }
        });
    }

    private void executeAfterJoinTasks(Collection<DATA> dataList) {
        afterJoinExecutorWithLevels.forEach(leveled -> {
            final AfterJoinExecutorWithLevel leveled1 = leveled;
            final List<Task> afterJoinTasks = dataList.stream().flatMap(data -> buildAfterJoinTasks(leveled1, data).stream()).collect(Collectors.toList());
            try {
                if (log.isDebugEnabled()) {
                    StopWatch stopwatch = new StopWatch("Starting executing after join tasks");
                    stopwatch.start();
                    this.executorService.invokeAll(afterJoinTasks);
                    stopwatch.stop();
                    log.debug("run execute cost {} ms, task is {}.", stopwatch.getTotalTimeMillis(), afterJoinTasks);
                } else {
                    this.executorService.invokeAll(afterJoinTasks);
                }
            } catch (InterruptedException e) {
                throw new JoinException(JoinErrorCode.AFTER_JOIN_ERROR, e);
            }
        });

    }

    @SuppressWarnings("unchecked")
    private List<Task> buildJoinTasks(JoinExecutorWithLevel leveledExecutors, Collection<DATA> dataList) {
        return leveledExecutors.getJoinFieldExecutors()
                .stream()
                .map(executor -> new Task(data -> executor.execute((Collection<DATA>) data), dataList, joinExceptionNotifier))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Task> buildAfterJoinTasks(AfterJoinExecutorWithLevel leveledExecutors, DATA data) {
        return leveledExecutors.getAfterJoinMethodExecutors()
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

    @AllArgsConstructor
    @Getter
    class JoinExecutorWithLevel {
        private final Integer level;
        private final List<JoinFieldExecutor<DATA>> joinFieldExecutors;
    }

    @AllArgsConstructor
    @Getter
    class AfterJoinExecutorWithLevel {
        private final Integer level;
        private final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors;
    }

}


