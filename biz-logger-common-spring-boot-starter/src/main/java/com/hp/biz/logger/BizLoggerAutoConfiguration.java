package com.hp.biz.logger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.hp.biz.logger.annotation.EnableBizLogger;
import com.hp.biz.logger.aop.BizLoggerAspect;
import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.exception.BizLoggerExceptionNotifier;
import com.hp.biz.logger.factory.BizLoggerBasedBizLogCreatorFactory;
import com.hp.biz.logger.factory.DefaultBizLogCreatorsExecutorFactory;
import com.hp.biz.logger.factory.IBizLogCreatorFactory;
import com.hp.biz.logger.factory.IBizLogCreatorsExecutorFactory;
import com.hp.biz.logger.function.AbstractBizLoggerFunctionResolver;
import com.hp.biz.logger.function.IBizLoggerFunctionRegistrar;
import com.hp.biz.logger.function.IBizLoggerFunctionResolver;
import com.hp.biz.logger.model.BizLogOperator;
import com.hp.biz.logger.service.IBizLogOperatorService;
import com.hp.biz.logger.service.IBizLogSyncService;
import com.hp.biz.logger.service.IBizLoggerPerformanceMonitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hp.biz.logger.function.JavaObjectDiffBasedBizDifferComponent.JavaObjectDiffBasedTypeComparisonConfigurer;

/**
 * @author hp
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(BizLoggerProperties.class)
public class BizLoggerAutoConfiguration implements ImportAware {

    private AnnotationAttributes enableBizLog;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableBizLog = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableBizLogger.class.getName(), false)
        );
        if (this.enableBizLog == null) {
            log.info("Annotate the spring boot application class with the @EnableBizLog to enable the biz logger.");
            return;
        }
        initMetaBizLogContext();
    }

    private void initMetaBizLogContext() {
        BizLoggerContext.putTenant(enableBizLog.getString("tenant"));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BizLoggerAspect bizLogAspect(
            ObjectProvider<IBizLoggerPerformanceMonitor> bizLogPerformanceMonitor,
            ObjectProvider<IBizLogCreatorsExecutorFactory> bizLogCreatorsExecutorFactory,
            ObjectProvider<IBizLogSyncService> bizLogSyncService
    ) {
        return new BizLoggerAspect(
                bizLogPerformanceMonitor,
                bizLogCreatorsExecutorFactory,
                bizLogSyncService
        );
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(IBizLogCreatorsExecutorFactory.class)
    public IBizLogCreatorsExecutorFactory bizLogCreatorsExecutorFactory(
            List<IBizLogCreatorFactory> bizLogCreatorFactories,
            Map<String, ExecutorService> executorServiceMap,
            BizLoggerExceptionNotifier bizLoggerExceptionNotifier,
            BeanFactory beanFactory
    ) {
        return new DefaultBizLogCreatorsExecutorFactory(
                enableBizLog,
                bizLogCreatorFactories,
                executorServiceMap,
                bizLoggerExceptionNotifier,
                new BeanFactoryResolver(beanFactory)
        );
    }

    @Bean
    @Role(BeanDefinition.ROLE_SUPPORT)
    public ExecutorService defaultBizLoggerExecutor() {
        final BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("BizLogger-Thread-%d")
                .daemon(true)
                .build();
        int maxSize = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                0,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                basicThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean
    @ConditionalOnMissingBean(BizLoggerExceptionNotifier.class)
    public BizLoggerExceptionNotifier bizLoggerExceptionNotifier() {
        return () -> (data, ex) -> {
            log.error("Biz Logger Exception: ", ex);
            log.error("Exception context={}", data);
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(IBizLoggerFunctionRegistrar.class)
    public IBizLoggerFunctionResolver bizLoggerFunctionResolver() {
        return new AbstractBizLoggerFunctionResolver(
                enableBizLog.getBoolean("overrideFunction")
        ) {
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(IBizLogCreatorFactory.class)
    public IBizLogCreatorFactory bizLogCreatorFactory(
            ObjectProvider<IBizLogOperatorService> bizLogOperatorService,
            ObjectProvider<IBizLoggerFunctionResolver> bizLoggerFunctionResolver
    ) {
        return new BizLoggerBasedBizLogCreatorFactory(bizLogOperatorService, bizLoggerFunctionResolver);
    }

    @Bean
    @Role(BeanDefinition.ROLE_APPLICATION)
    @ConditionalOnMissingBean(IBizLogOperatorService.class)
    public IBizLogOperatorService bizLogOperatorService() {
        return () -> new BizLogOperator("", "BizLogger-Default-Operator");
    }

    @Bean
    @Role(BeanDefinition.ROLE_SUPPORT)
    @ConditionalOnMissingBean(IBizLoggerPerformanceMonitor.class)
    public IBizLoggerPerformanceMonitor bizLogPerformanceMonitor() {
        return stopWatch -> log.debug("BizLogger performance: {}", stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    @Bean
    @Role(BeanDefinition.ROLE_APPLICATION)
    @ConditionalOnMissingBean(IBizLogSyncService.class)
    public IBizLogSyncService bizLogSyncService() {
        return logs -> {
            if (CollUtil.isEmpty(logs)) {
                return;
            }
            final Logger logger = LoggerFactory.getLogger("Biz-Logger");
            logs.forEach(bizLog -> logger.info("{}", JSONUtil.toJsonPrettyStr(bizLog)));
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_APPLICATION)
    @ConditionalOnMissingBean(JavaObjectDiffBasedTypeComparisonConfigurer.class)
    public JavaObjectDiffBasedTypeComparisonConfigurer javaObjectDiffBasedTypeComparisonConfigurer() {
        return () -> Lists.newArrayList(LocalDateTime.class, Instant.class, LocalDate.class, Date.class);
    }
}
