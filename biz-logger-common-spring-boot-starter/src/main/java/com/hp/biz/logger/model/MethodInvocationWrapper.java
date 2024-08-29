package com.hp.biz.logger.model;

import com.google.common.base.Preconditions;
import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.service.IBizLoggerPerformanceMonitor;
import com.hp.common.base.annotation.FieldDesc;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hp
 */
public class MethodInvocationWrapper {

    @FieldDesc("代理方法")
    private final ProceedingJoinPoint joinPoint;
    @FieldDesc("计时器")
    private final StopWatch stopWatch;

    @FieldDesc("方法所在类")
    @Getter
    private final Class<?> targetClass;

    @FieldDesc("调用方法")
    @Getter
    private final Method method;

    @FieldDesc("方法入参")
    @Getter
    private final Object[] args;

    @FieldDesc("调用是否成功")
    private boolean success = false;

    @FieldDesc("方法出参")
    @Getter
    private Object result = null;

    @FieldDesc("方法抛出的异常")
    @Getter
    private Throwable throwable;

    public MethodInvocationWrapper(ProceedingJoinPoint joinPoint, StopWatch stopWatch) {
        Preconditions.checkArgument(Objects.nonNull(joinPoint), "The joinPoint cant be null.");
        Preconditions.checkArgument(Objects.nonNull(stopWatch), "The stopWatch cant be null.");
        Preconditions.checkArgument(joinPoint.getSignature() instanceof MethodSignature, "The ProceedingJoinPoint has to be a MethodSignature type.");
        this.joinPoint = joinPoint;
        this.targetClass = getTargetClass(joinPoint.getTarget());
        this.method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        this.args = joinPoint.getArgs();
        this.stopWatch = stopWatch;
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
    }

    public void proceed() {
        stopWatch.start(IBizLoggerPerformanceMonitor.MONITOR_TASK_INVOCATION);
        try {
            this.result = joinPoint.proceed();
            this.success = true;
            BizLoggerContext.putReturnValue(this.result);
        } catch (Throwable e) {
            this.success = false;
            this.throwable = e;
            BizLoggerContext.putThrowable(this.throwable);
        }
        stopWatch.stop();
        BizLoggerContext.putInvocationInfo(new InvocationInfo(
                this.targetClass,
                this.method,
                this.args,
                this.success,
                this.result,
                this.throwable,
                stopWatch.lastTaskInfo().getTimeMillis()
        ));
    }

    public boolean failed() {
        return Objects.nonNull(this.throwable) || !this.success;
    }

    private static Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    public void throwException() throws Throwable {
        if (Objects.isNull(this.throwable)) {
            return;
        }
        throw this.throwable;
    }
}
