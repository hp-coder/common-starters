package com.hp.joininmemory.support;

import com.google.common.base.Stopwatch;
import com.hp.joininmemory.JoinFieldExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class ParallelJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {
    private final ExecutorService executorService;
    private final List<JoinExecutorWithLevel> joinExecutorWithLevels;


    public ParallelJoinFieldsExecutor(Class<DATA> clazz,
                                      List<JoinFieldExecutor<DATA>> executors,
                                      ExecutorService executorService
    ) {
        super(clazz, executors);
        this.executorService = executorService;
        this.joinExecutorWithLevels = buildJoinExecutorWithLevel();
    }

    private List<JoinExecutorWithLevel> buildJoinExecutorWithLevel() {
        return getJoinFieldExecutors().stream()
                .collect(Collectors.groupingBy(JoinFieldExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new JoinExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(i -> i.level))
                .collect(Collectors.toList());
    }

    @Override
    public void execute(List<DATA> dataList) {
        this.joinExecutorWithLevels.forEach(leveledExecutors -> {
            log.debug("run join on level {} use {}", leveledExecutors.getLevel(),
                    leveledExecutors.getJoinFieldExecutors());
            List<Task> tasks = buildTasks(leveledExecutors, dataList);
            try {
                if (log.isDebugEnabled()){
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    this.executorService.invokeAll(tasks);
                    stopwatch.stop();
                    log.debug("run execute cost {} ms, task is {}.", stopwatch.elapsed(TimeUnit.MILLISECONDS), tasks);
                }else{
                    this.executorService.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                log.error("invoke task {} interrupted", tasks, e);
            }
        });

    }

    private List<Task> buildTasks(JoinExecutorWithLevel leveledExecutors, List<DATA> dataList) {
        return leveledExecutors.getJoinFieldExecutors()
                .stream()
                .map(executor -> new Task(executor, dataList))
                .collect(Collectors.toList());
    }


    @AllArgsConstructor
    class Task implements Callable<Void> {

        private final JoinFieldExecutor<DATA> joinFieldExecutor;
        private final List<DATA> dataList;

        @Override
        public Void call() {
            this.joinFieldExecutor.execute(dataList);
            return null;
        }
    }

    @AllArgsConstructor
    @Getter
    class JoinExecutorWithLevel {
        private final Integer level;
        private final List<JoinFieldExecutor<DATA>> joinFieldExecutors;
    }
}


