package com.hp.biz.logger.factory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.hp.biz.logger.annotation.EnableBizLogger;
import com.hp.biz.logger.exception.BizLoggerExceptionNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.expression.BeanResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author hp
 */
@Slf4j
public class DefaultBizLogCreatorsExecutorFactory implements IBizLogCreatorsExecutorFactory {
    private final Map<AnnotatedElementKey, IBizLogCreatorsExecutor> cache = Maps.newConcurrentMap();
    private final AnnotationAttributes enableBizLog;
    private final List<IBizLogCreatorFactory> bizLogCreatorFactories;
    private final Map<String, ExecutorService> executorServiceMap;
    private final BizLoggerExceptionNotifier bizLoggerExceptionNotifier;
    private final BeanResolver beanResolver;

    public DefaultBizLogCreatorsExecutorFactory(
            AnnotationAttributes enableBizLog,
            List<IBizLogCreatorFactory> bizLogCreatorFactories,
            Map<String, ExecutorService> executorServiceMap,
            BizLoggerExceptionNotifier bizLoggerExceptionNotifier,
            BeanResolver beanResolver
    ) {
        this.enableBizLog = enableBizLog;
        this.bizLogCreatorFactories = bizLogCreatorFactories;
        AnnotationAwareOrderComparator.sort(this.bizLogCreatorFactories);
        this.executorServiceMap = executorServiceMap;
        this.bizLoggerExceptionNotifier = bizLoggerExceptionNotifier;
        this.beanResolver = beanResolver;
    }

    @Override
    public IBizLogCreatorsExecutor createFor(Class<?> targetClass, Method method) {
        final AnnotatedElementKey key = new AnnotatedElementKey(method, targetClass);
        return this.cache.computeIfAbsent(key, k -> createCreatorsExecutor(targetClass, method));
    }

    private IBizLogCreatorsExecutor createCreatorsExecutor(Class<?> targetClass, Method method) {
        final List<IBizLogCreator> creators = this.bizLogCreatorFactories.stream()
                .flatMap(factory -> factory.createFor(targetClass, method).stream())
                .toList();

        return buildCreatorsExecutor(targetClass, method, enableBizLog, creators);
    }

    private IBizLogCreatorsExecutor buildCreatorsExecutor(Class<?> targetClass, Method method, AnnotationAttributes enableBizLog, List<IBizLogCreator> creators) {
        if (enableBizLog == null || enableBizLog.getEnum("executorType") == EnableBizLogger.BizLoggerExecutorType.SERIAL) {
            log.debug("Biz Logger for {}.{} uses serial executor", targetClass, method);
            return new SerialBizLogCreatorExecutor(beanResolver, creators);
        }
        final String executorName = enableBizLog.getString("executorName");
        if (enableBizLog.getEnum("executorType") == EnableBizLogger.BizLoggerExecutorType.PARALLEL) {
            log.debug("Biz Logger for {}.{} uses parallel executor, the executor pool is {}", targetClass, method, executorName);
            final ExecutorService executorService = executorServiceMap.get(executorName);
            Preconditions.checkArgument(executorService != null, "The required executorService=%s doesn't exist.".formatted(executorName));
            return new ParallelBizLogCreatorExecutor(
                    beanResolver,
                    creators,
                    executorService,
                    bizLoggerExceptionNotifier
            );
        }
        throw new IllegalArgumentException("无效类型");
    }
}
