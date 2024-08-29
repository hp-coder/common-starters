package com.hp.biz.logger.aop;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.factory.IBizLogCreatorsExecutorFactory;
import com.hp.biz.logger.model.BizLogDTO;
import com.hp.biz.logger.model.MethodInvocationWrapper;
import com.hp.biz.logger.service.IBizLogSyncService;
import com.hp.biz.logger.service.IBizLoggerPerformanceMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.hp.biz.logger.service.IBizLoggerPerformanceMonitor.*;


/**
 * @author hp
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class BizLoggerAspect {

    private final ObjectProvider<IBizLoggerPerformanceMonitor> bizLogPerformanceMonitor;

    private final ObjectProvider<IBizLogCreatorsExecutorFactory> bizLogCreatorsExecutorFactory;

    private final ObjectProvider<IBizLogSyncService> bizLogSyncService;

    @Around("@annotation(com.hp.biz.logger.annotation.BizLoggers) || @annotation(com.hp.biz.logger.annotation.BizLogger)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        BizLoggerContext.addEmptyFrame();
        final StopWatch stopWatch = new StopWatch(MONITOR_NAME);

        final MethodInvocationWrapper invocationWrapper = new MethodInvocationWrapper(joinPoint, stopWatch);
        final Collection<BizLogDTO> preInvocationLogs = preInvocation(invocationWrapper, stopWatch);

        invocationWrapper.proceed();

        final Collection<BizLogDTO> postInvocationLogs = postInvocation(invocationWrapper, stopWatch);

        syncLogs(preInvocationLogs, postInvocationLogs, stopWatch);

        performanceMonitor(stopWatch);

        if (invocationWrapper.failed()) {
            invocationWrapper.throwException();
        }
        return invocationWrapper.getResult();
    }

    private void syncLogs(Collection<BizLogDTO> preInvocationLogs, Collection<BizLogDTO> postInvocationLogs, StopWatch stopWatch) {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        final List<BizLogDTO> logs = Lists.newArrayList();
        if(CollUtil.isNotEmpty(preInvocationLogs)){
            logs.addAll(preInvocationLogs);
        }
        if(CollUtil.isNotEmpty(postInvocationLogs)){
            logs.addAll(postInvocationLogs);
        }
        stopWatch.start(MONITOR_TASK_SYNC_LOGS);
        try {
            Optional.ofNullable(bizLogSyncService.getIfAvailable()).ifPresent(service -> service.sync(logs));
        } catch (Exception e) {
            log.error("BizLoggerAspect.doAround - syncLogs - Sync logs failed:", e);
        } finally {
            stopWatch.stop();
        }
    }

    private Collection<BizLogDTO> preInvocation(MethodInvocationWrapper invocationWrapper, StopWatch stopWatch) {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        stopWatch.start(MONITOR_TASK_BEFORE_INVOCATION);
        Collection<BizLogDTO> bizLogDTOs = Collections.emptyList();
        try {
            bizLogDTOs = Optional.ofNullable(bizLogCreatorsExecutorFactory.getIfAvailable())
                    .map(factory -> factory.createFor(invocationWrapper.getTargetClass(), invocationWrapper.getMethod()))
                    .map(executor -> executor.execute(invocationWrapper, true))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            log.error("BizLoggerAspect.doAround - preInvocation - Creating BizLogCreators failed:", e);
        } finally {
            stopWatch.stop();
        }
        return bizLogDTOs;
    }

    private Collection<BizLogDTO> postInvocation(MethodInvocationWrapper invocationWrapper, StopWatch stopWatch) {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        stopWatch.start(MONITOR_TASK_AFTER_INVOCATION);
        Collection<BizLogDTO> bizLogDTOs = Collections.emptyList();
        try {
            bizLogDTOs = Optional.ofNullable(bizLogCreatorsExecutorFactory.getIfAvailable())
                    .map(factory -> factory.createFor(invocationWrapper.getTargetClass(), invocationWrapper.getMethod()))
                    .map(executor -> executor.execute(invocationWrapper, false))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            log.error("BizLoggerAspect.doAround - postInvocation - Creating BizLogCreators failed:", e);
        } finally {
            stopWatch.stop();
        }
        return bizLogDTOs;
    }

    private void performanceMonitor(StopWatch stopWatch){
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        try {
            Optional.ofNullable(this.bizLogPerformanceMonitor.getIfAvailable())
                    .ifPresent(monitor -> monitor.print(stopWatch));
        } catch (Exception e) {
            log.error("BizLoggerAspect.doAround - postInvocation - Monitoring biz logger performance failed:", e);
        }
    }
}
