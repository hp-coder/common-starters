package com.hp.joininmemory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.joininmemory.JoinFieldExecutor;
import com.hp.joininmemory.JoinFieldExecutorFactory;
import com.hp.joininmemory.JoinFieldsExecutor;
import com.hp.joininmemory.JoinFieldsExecutorFactory;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.annotation.JoinInMemoryExecutorType;
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
    private final Map<String, ExecutorService> executorServiceMap;

    public DefaultJoinFieldsExecutorFactory(Collection<? extends JoinFieldExecutorFactory> joinFieldExecutorFactories,
                                            Map<String, ExecutorService> executorServiceMap) {
        this.joinFieldExecutorFactories = Lists.newArrayList(joinFieldExecutorFactories);
        //执行顺序排序
        AnnotationAwareOrderComparator.sort(this.joinFieldExecutorFactories);
        this.executorServiceMap = executorServiceMap;
    }

    @Override
    public <DATA> JoinFieldsExecutor<DATA> createFor(Class<DATA> clazz) {
        final List<JoinFieldExecutor<DATA>> joinItemExecutors = this.joinFieldExecutorFactories.stream()
                //通过对应类型的数据装载执行器工厂创建执行器
                .flatMap(factory -> factory.createForType(clazz).stream())
                .collect(Collectors.toList());
        //join配置，类级别，指定串并行及线程池等做资源隔离
        final JoinInMemoryConfig joinInMemoryConfig = clazz.getAnnotation(JoinInMemoryConfig.class);
        return buildJoinFieldsExecutor(clazz, joinItemExecutors, joinInMemoryConfig);
    }

    private <DATA> JoinFieldsExecutor<DATA> buildJoinFieldsExecutor(Class<DATA> clazz, List<JoinFieldExecutor<DATA>> joinItemExecutors, JoinInMemoryConfig joinInMemoryConfig) {
        if (joinInMemoryConfig == null || joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.SERIAL) {
            log.info("JoinInMemory for {} uses parallel serial executor", clazz);
            return new SerialJoinFieldsExecutor<>(clazz, joinItemExecutors);
        }
        if (joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.PARALLEL) {
            log.info("JoinInMemory for {} uses parallel executor, the executor pool is {}", clazz, joinInMemoryConfig.executorName());
            ExecutorService executor = executorServiceMap.get(joinInMemoryConfig.executorName());
            Preconditions.checkArgument(executor != null);
            return new ParallelJoinFieldsExecutor<>(clazz, joinItemExecutors, executor);
        }
        throw new IllegalArgumentException("无效类型");
    }
}
