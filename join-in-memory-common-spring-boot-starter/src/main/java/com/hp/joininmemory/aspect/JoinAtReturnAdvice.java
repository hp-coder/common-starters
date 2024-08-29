package com.hp.joininmemory.aspect;

import cn.hutool.core.util.StrUtil;
import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.annotation.JoinAtReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 * @since 1.0.1-sp3.2-SNAPSHOT
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class JoinAtReturnAdvice {

    private final JoinService joinService;

    private final SpELHelper spELHelper;

    @Pointcut(value = "@annotation(com.hp.joininmemory.annotation.JoinAtReturn)")
    public void joinAtReturn() {
    }

    @AfterReturning(value = "joinAtReturn()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        if (!(joinPoint.getSignature() instanceof MethodSignature methodSignature)) {
           return;
        }
        final Method method = methodSignature.getMethod();
        final JoinAtReturn joinAtReturn = method.getAnnotation(JoinAtReturn.class);

        Object joinData = returnValue;
        if (Objects.isNull(joinData)) {
            return;
        }
        if (joinData instanceof Optional<?> optionalVal) {
            if (optionalVal.isEmpty()) {
                return;
            } else {
                joinData = optionalVal.get();
            }
        }
        if (StrUtil.isNotEmpty(joinAtReturn.value())) {
            joinData = spELHelper.newGetterInstance(joinAtReturn.value()).apply(joinData);
        }
        if (Objects.isNull(joinData)) {
            return;
        }
        if (Collection.class.isAssignableFrom(joinData.getClass())) {
            joinService.joinInMemory((Collection<?>) joinData);
        } else {
            joinService.joinInMemory(joinData);
        }
    }
}
