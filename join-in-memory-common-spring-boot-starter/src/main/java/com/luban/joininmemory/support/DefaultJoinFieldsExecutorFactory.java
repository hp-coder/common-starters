package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.luban.joininmemory.*;
import com.luban.joininmemory.annotation.JoinInMemoryConfig;
import com.luban.joininmemory.constant.JoinInMemoryExecutorType;
import com.luban.joininmemory.context.JoinContext;
import com.luban.joininmemory.exception.ExceptionNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class DefaultJoinFieldsExecutorFactory implements JoinFieldsExecutorFactory {

    private final List<JoinFieldExecutorFactory> joinFieldExecutorFactories;
    private final List<AfterJoinMethodExecutorFactory> afterJoinMethodExecutorFactories;
    private final Map<String, ExecutorService> executorServiceMap;
    private final ExceptionNotifier joinExceptionNotifier;
    private final ExceptionNotifier afterJoinExceptionNotifier;

    public DefaultJoinFieldsExecutorFactory(
            Collection<? extends JoinFieldExecutorFactory> joinFieldExecutorFactories,
            Collection<? extends AfterJoinMethodExecutorFactory> afterJoinMethodExecutorFactories,
            Map<String, ExecutorService> executorServiceMap,
            ExceptionNotifier joinExceptionNotifier,
            ExceptionNotifier afterJoinExceptionNotifier
    ) {
        this.joinFieldExecutorFactories = Lists.newArrayList(joinFieldExecutorFactories);
        this.afterJoinMethodExecutorFactories = Lists.newArrayList(afterJoinMethodExecutorFactories);
        AnnotationAwareOrderComparator.sort(this.joinFieldExecutorFactories);
        AnnotationAwareOrderComparator.sort(this.afterJoinMethodExecutorFactories);
        this.executorServiceMap = executorServiceMap;
        this.joinExceptionNotifier = joinExceptionNotifier;
        this.afterJoinExceptionNotifier = afterJoinExceptionNotifier;
    }

    @Override
    public <DATA> JoinFieldsExecutor<DATA> createFor(Class<DATA> clazz) {
        final JoinInMemoryConfig joinInMemoryConfig = clazz.getAnnotation(JoinInMemoryConfig.class);
        final JoinContext<DATA> joinContext = new JoinContext<>(clazz, joinInMemoryConfig);

        final List<JoinFieldExecutor<DATA>> joinItemExecutors = this.joinFieldExecutorFactories.stream()
                .flatMap(factory -> factory.createForType(joinContext).stream())
                .collect(Collectors.toList());

        final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors = this.afterJoinMethodExecutorFactories.stream()
                .flatMap(factory -> factory.createForType(joinContext).stream())
                .collect(Collectors.toList());

        return buildJoinFieldsExecutor(clazz, joinInMemoryConfig, joinItemExecutors, afterJoinMethodExecutors);
    }

    private <DATA> JoinFieldsExecutor<DATA> buildJoinFieldsExecutor(
            Class<DATA> clazz,
            JoinInMemoryConfig joinInMemoryConfig,
            List<JoinFieldExecutor<DATA>> joinFieldExecutors,
            List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors
    ) {
        if (joinInMemoryConfig == null || joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.SERIAL) {
            log.debug("JoinInMemory for {} uses serial executor", clazz);
            return new SerialJoinFieldsExecutor<>(clazz, joinFieldExecutors, afterJoinMethodExecutors);
        }
        if (joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.PARALLEL) {
            log.debug("JoinInMemory for {} uses parallel executor, the executor pool is {}", clazz, joinInMemoryConfig.executorName());
            ExecutorService executor = executorServiceMap.get(joinInMemoryConfig.executorName());
            Preconditions.checkArgument(executor != null);
            return new ParallelJoinFieldsExecutor<>(clazz,
                    joinFieldExecutors,
                    afterJoinMethodExecutors,
                    executor,
                    joinExceptionNotifier,
                    afterJoinExceptionNotifier
            );
        }
        throw new IllegalArgumentException("无效类型");
    }
}
