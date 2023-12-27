package com.luban.joininmemory.support;

import com.luban.joininmemory.AfterJoinMethodExecutor;
import com.luban.joininmemory.JoinFieldExecutor;
import com.luban.joininmemory.exception.JoinErrorCode;
import com.luban.joininmemory.exception.JoinException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

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


    public ParallelJoinFieldsExecutor(Class<DATA> clazz,
                                      List<JoinFieldExecutor<DATA>> joinFieldExecutors,
                                      List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors,
                                      ExecutorService executorService
    ) {
        super(clazz, joinFieldExecutors, afterJoinMethodExecutors);
        this.executorService = executorService;
        this.joinExecutorWithLevels = buildJoinExecutorWithLevel();
        this.afterJoinExecutorWithLevels = buildAfterJoinExecutorWithLevel();
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
    public void execute(List<DATA> dataList) {
        executeJoinTasks(dataList);
        executeAfterJoinTasks(dataList);
    }

    private void executeJoinTasks(List<DATA> dataList) {
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
            } catch (Exception e) {
                throw new JoinException(JoinErrorCode.JOIN_ERROR, e);
            }
        });
    }

    private void executeAfterJoinTasks(List<DATA> dataList) {
        try {
            final List<Task> afterJoinTasks = afterJoinExecutorWithLevels
                    .stream()
                    .flatMap(leveledExecutors ->
                            dataList.stream()
                                    .flatMap(data -> buildAfterJoinTasks(leveledExecutors, data).stream())
                    )
                    .collect(Collectors.toList());
            if (log.isDebugEnabled()) {
                StopWatch stopwatch = new StopWatch("Starting executing after join tasks");
                stopwatch.start();
                this.executorService.invokeAll(afterJoinTasks);
                stopwatch.stop();
                log.debug("run execute cost {} ms, task is {}.", stopwatch.getTotalTimeMillis(), afterJoinTasks);
            } else {
                this.executorService.invokeAll(afterJoinTasks);
            }
        } catch (Exception e) {
            throw new JoinException(JoinErrorCode.AFTER_JOIN_ERROR, e);
        }
    }

    private List<Task> buildJoinTasks(JoinExecutorWithLevel leveledExecutors, List<DATA> dataList) {
        return leveledExecutors.getJoinFieldExecutors()
                .stream()
                .map(executor -> new Task(data -> executor.execute((List<DATA>) data), dataList))
                .collect(Collectors.toList());
    }

    private List<Task> buildAfterJoinTasks(AfterJoinExecutorWithLevel leveledExecutors, DATA data) {
        return leveledExecutors.getAfterJoinMethodExecutors()
                .stream()
                .map(executor -> new Task(d -> executor.execute((DATA) d), data))
                .collect(Collectors.toList());
    }


    @AllArgsConstructor
    static class Task implements Callable<Void> {

        private final Consumer<Object> consumer;
        private final Object data;

        @Override
        public Void call() {
            try {
                consumer.accept(data);
            } catch (Exception e) {
                throw new RuntimeException("JoinFieldExecutor failed for: ", e);
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


