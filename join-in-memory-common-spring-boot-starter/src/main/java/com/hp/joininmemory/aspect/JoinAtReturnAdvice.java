package com.hp.joininmemory.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.annotation.JoinAtReturn;
import com.hp.joininmemory.utils.JoinHelper;
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
 * Aspect for join at return
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
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
        log.debug("Getting Join At Return Method: {}", method.getName());

        final JoinAtReturn joinAtReturn = method.getAnnotation(JoinAtReturn.class);
        // 获取方法返回值
        Object joinData = returnValue;
        if (Objects.isNull(joinData)) {
            log.debug("Join At Return Data is Null");
            return;
        }
        // optional support
        if (joinData instanceof Optional<?> optionalVal) {
            log.debug("Join At Return Data is a Optional");
            if (optionalVal.isEmpty()) {
                log.debug("Join At Return Optional Data is Empty");
                return;
            } else {
                joinData = optionalVal.get();
            }
        }
        // extract join data. the data is supposed to be a collection or a single object instance
        if (StrUtil.isNotEmpty(joinAtReturn.value())) {
            log.debug("Getting Join At Return Data From Expression");
            joinData = spELHelper.newGetterInstance(joinAtReturn.value()).apply(joinData);
        }
        if (Objects.isNull(joinData)) {
            log.debug("Expression Processed Join At Return Data is Null");
            return;
        }
        // dynamic fields
        determineDynamicFields(joinAtReturn, joinData);

        if (Collection.class.isAssignableFrom(joinData.getClass())) {
            joinService.joinInMemory((Collection<?>) joinData);
        } else {
            joinService.joinInMemory(joinData);
        }
    }

    /**
     * 根据 JoinAtReturn 注解的 included 和 excluded 参数确定动态字段
     * <p>
     * 此方法根据注解中指定的包含和排除字段列表，为关联查询设置动态字段过滤规则。 如果joinData是集合类型，则获取集合元素的类型；否则直接使用对象类型。
     *
     * @param joinAtReturn JoinAtReturn注解实例，包含included和excluded字段配置
     * @param joinData     需要进行关联查询的数据对象，可能是单个对象或集合
     */
    private void determineDynamicFields(JoinAtReturn joinAtReturn, Object joinData) {
        final String[] included = joinAtReturn.included();
        final String[] excluded = joinAtReturn.excluded();

        // 处理包含字段列表
        if (ArrayUtil.isNotEmpty(included)) {
            log.debug("Setting Join At Return Dynamic Include Join Fields");
            if (joinData instanceof Collection<?> col) {
                Class<?> elementType = col.iterator().next().getClass();
                JoinHelper.includeFields(elementType.getSimpleName(), included);
            } else {
                final String className = joinData.getClass().getSimpleName();
                JoinHelper.includeFields(className, included);
            }
        }

        // 处理排除字段列表
        if (ArrayUtil.isNotEmpty(excluded)) {
            log.debug("Setting Join At Return Dynamic Exclude Join Fields");
            if (joinData instanceof Collection<?> col) {
                Class<?> elementType = col.iterator().next().getClass();
                JoinHelper.excludeFields(elementType.getSimpleName(), excluded);
            } else {
                final String className = joinData.getClass().getSimpleName();
                JoinHelper.excludeFields(className, excluded);
            }
        }
    }

}
