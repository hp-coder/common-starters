package com.hp.biz.logger.factory;

import cn.hutool.core.collection.CollUtil;
import com.hp.biz.logger.exception.BizLoggerErrorCode;
import com.hp.biz.logger.exception.BizLoggerException;
import com.hp.biz.logger.exception.BizLoggerExceptionNotifier;
import com.hp.biz.logger.model.BizLogDTO;
import com.hp.biz.logger.model.MethodInvocationWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hp
 */
@Slf4j
public class ParallelBizLogCreatorExecutor extends AbstractBizLogCreatorsExecutor {

    private final ExecutorService executorService;
    private final BizLoggerExceptionNotifier exceptionNotifier;
    private final List<ExecutorWithLevel> executorWithLevels;


    public ParallelBizLogCreatorExecutor(
            BeanResolver beanResolver,
            List<IBizLogCreator> creators,
            ExecutorService executorService,
            BizLoggerExceptionNotifier exceptionNotifier
    ) {
        super(beanResolver, creators);
        this.executorService = executorService;
        this.exceptionNotifier = exceptionNotifier;
        this.executorWithLevels = buildExecutorWithLevel();
    }

    private List<ExecutorWithLevel> buildExecutorWithLevel() {
        return getCreators()
                .stream()
                .collect(Collectors.groupingBy(IBizLogCreator::runOnLevel))
                .entrySet()
                .stream()
                .map(entry -> new ExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ExecutorWithLevel::level))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BizLogDTO> execute(MethodInvocationWrapper invocationWrapper, boolean preInvocation) {
        final EvaluationContext evaluationContext = BizLoggerEvaluationContextFactory.createBizLoggerEvaluationContext(
                invocationWrapper.getMethod(),
                invocationWrapper.getArgs(),
                invocationWrapper.getTargetClass(),
                getBeanResolver()
        );

        return executeCreation(evaluationContext, preInvocation);
    }

    private List<BizLogDTO> executeCreation(EvaluationContext evaluationContext, boolean preInvocation) {
        return this.executorWithLevels.stream()
                .flatMap(leveledTasks -> {
                    log.debug("Run creation on level {} use {}", leveledTasks.level(), leveledTasks.creators());
                    final List<Task> tasks = buildCreationTasks(leveledTasks, evaluationContext, preInvocation);
                    if (CollUtil.isEmpty(tasks)) {
                        return Stream.empty();
                    }
                    try {
                        if (log.isDebugEnabled()) {
                            StopWatch stopwatch = new StopWatch("Starting executing creation tasks");
                            stopwatch.start();
                            final Stream<BizLogDTO> bizLogDTOs = this.executorService.invokeAll(tasks)
                                    .stream()
                                    .map(f -> {
                                        try {
                                            return f.get();
                                        } catch (InterruptedException | ExecutionException e) {
                                            exceptionNotifier.handle().accept(evaluationContext, e);
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull);
                            stopwatch.stop();
                            log.debug("Run execute cost {} ms, task is {}.", stopwatch.getTotalTimeMillis(), tasks);
                            return bizLogDTOs;
                        } else {
                            return this.executorService.invokeAll(tasks)
                                    .stream()
                                    .map(f -> {
                                        try {
                                            return f.get();
                                        } catch (InterruptedException | ExecutionException e) {
                                            exceptionNotifier.handle().accept(evaluationContext, e);
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull);
                        }
                    } catch (InterruptedException e) {
                        throw new BizLoggerException(BizLoggerErrorCode.async_creation_error, e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Task> buildCreationTasks(ExecutorWithLevel leveledExecutors, EvaluationContext evaluationContext, boolean preInvocation) {
        return leveledExecutors.creators()
                .stream()
                .filter(c -> c.preInvocation() == preInvocation)
                .map(executor -> new Task(executor::createLog, evaluationContext, exceptionNotifier))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    static class Task implements Callable<BizLogDTO> {

        private final Function<EvaluationContext, BizLogDTO> function;
        private final EvaluationContext evaluationContext;
        private final BizLoggerExceptionNotifier exceptionNotifier;

        @Override
        public BizLogDTO call() {
            try {
                return function.apply(evaluationContext);
            } catch (Exception e) {
                exceptionNotifier.handle().accept(evaluationContext, e);
            }
            return null;
        }
    }

    record ExecutorWithLevel(Integer level, List<IBizLogCreator> creators) {
    }

}
