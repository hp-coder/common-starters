package com.hp.spiderman.factory.support;

import com.google.common.base.Stopwatch;
import com.hp.spiderman.factory.DomNodeExecutor;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public class MultiverseDomNodesExecutor<DATA> extends AbstractDomNodesExecutor<DATA> {
    private final ExecutorService executorService;
    private final List<ExecutorWithLevel> executorWithLevels;

    public MultiverseDomNodesExecutor(Class<DATA> clazz, List<DomNodeExecutor<DATA>> spiderNodeExecutors, ExecutorService executorService) {
        super(clazz, spiderNodeExecutors);
        this.executorService = executorService;
        this.executorWithLevels = buildExecutorWithLevel();
    }

    private List<ExecutorWithLevel> buildExecutorWithLevel() {
        return getSpiderNodeExecutors()
                .stream()
                .collect(Collectors.groupingBy(DomNodeExecutor::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new ExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(i -> i.level))
                .collect(Collectors.toList());
    }

    @Override
    public List<DATA> execute(List<Connection> connections) {
        return connections.stream()
                .map(connection -> {
                    try {
                        final Document doc = connection.get();
                        log.info("请求地址:{}", doc.location());
                        final DATA data = ConstructorUtils.invokeConstructor(getClazz());
                        this.executorWithLevels
                                .forEach(e -> {
                                    final List<Task> tasks = buildTasks(e, data, doc);
                                    log.debug("run join on level {} use {}", e.getLevel(), e.getJoinFieldExecutors());
                                    try {
                                        if (log.isDebugEnabled()) {
                                            Stopwatch stopwatch = Stopwatch.createStarted();
                                            this.executorService.invokeAll(tasks);
                                            stopwatch.stop();
                                            log.debug("run execute cost {} ms, task is {}.", stopwatch.elapsed(TimeUnit.MILLISECONDS), tasks);
                                        } else {
                                            this.executorService.invokeAll(tasks);
                                        }
                                    } catch (InterruptedException exception) {
                                        log.error("invoke task {} interrupted", tasks, exception);
                                    }
                                });
                        return data;
                    } catch (Exception e) {
                        log.error(" Can't initialize the instance of {} due to lack of no-arg constructor presents", getClazz());
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<Task> buildTasks(ExecutorWithLevel executorWithLevel, DATA data, Document document) {
        return executorWithLevel.getJoinFieldExecutors()
                .stream()
                .map(executor -> new Task(executor, data, document))
                .collect(Collectors.toList());
    }


    @AllArgsConstructor
    class Task implements Callable<Void> {

        private final DomNodeExecutor<DATA> spiderNodeExecutor;
        private final DATA data;
        private final Document document;

        @Override
        public Void call() {
            this.spiderNodeExecutor.execute(data, document);
            return null;
        }
    }

    @Value
    class ExecutorWithLevel {
        Integer level;
        List<DomNodeExecutor<DATA>> joinFieldExecutors;
    }
}
