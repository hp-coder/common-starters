package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.luban.joininmemory.*;
import com.luban.joininmemory.annotation.JoinInMemoryConfig;
import com.luban.joininmemory.annotation.JoinInMemoryExecutorType;
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

    public DefaultJoinFieldsExecutorFactory(
            Collection<? extends JoinFieldExecutorFactory> joinFieldExecutorFactories,
            Collection<? extends AfterJoinMethodExecutorFactory> afterJoinMethodExecutorFactories,
            Map<String, ExecutorService> executorServiceMap
    ) {
        this.joinFieldExecutorFactories = Lists.newArrayList(joinFieldExecutorFactories);
        this.afterJoinMethodExecutorFactories = Lists.newArrayList(afterJoinMethodExecutorFactories);
        AnnotationAwareOrderComparator.sort(this.joinFieldExecutorFactories);
        AnnotationAwareOrderComparator.sort(this.afterJoinMethodExecutorFactories);
        this.executorServiceMap = executorServiceMap;
    }

    @Override
    public <DATA> JoinFieldsExecutor<DATA> createFor(Class<DATA> clazz) {
        final List<JoinFieldExecutor<DATA>> joinItemExecutors = this.joinFieldExecutorFactories.stream()
                .flatMap(factory -> factory.createForType(clazz).stream())
                .collect(Collectors.toList());

        final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors = this.afterJoinMethodExecutorFactories.stream()
                .flatMap(factory -> factory.createForType(clazz).stream())
                .collect(Collectors.toList());

        final JoinInMemoryConfig joinInMemoryConfig = clazz.getAnnotation(JoinInMemoryConfig.class);
        return buildJoinFieldsExecutor(clazz, joinInMemoryConfig, joinItemExecutors, afterJoinMethodExecutors);
    }

    private <DATA> JoinFieldsExecutor<DATA> buildJoinFieldsExecutor(
            Class<DATA> clazz,
            JoinInMemoryConfig joinInMemoryConfig,
            List<JoinFieldExecutor<DATA>> joinItemExecutors,
            List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors
    ) {
        if (joinInMemoryConfig == null || joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.SERIAL) {
            log.info("JoinInMemory for {} uses parallel serial executor", clazz);
            return new SerialJoinFieldsExecutor<>(clazz, joinItemExecutors, afterJoinMethodExecutors);
        }
        if (joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.PARALLEL) {
            log.info("JoinInMemory for {} uses parallel executor, the executor pool is {}", clazz, joinInMemoryConfig.executorName());
            ExecutorService executor = executorServiceMap.get(joinInMemoryConfig.executorName());
            Preconditions.checkArgument(executor != null);
            return new ParallelJoinFieldsExecutor<>(clazz, joinItemExecutors, afterJoinMethodExecutors, executor);
        }
        throw new IllegalArgumentException("无效类型");
    }
}
