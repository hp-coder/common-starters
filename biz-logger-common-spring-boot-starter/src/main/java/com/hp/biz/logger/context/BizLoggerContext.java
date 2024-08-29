package com.hp.biz.logger.context;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.hp.biz.logger.BizLoggerProperties;
import com.hp.biz.logger.model.BizDiffObjectWrapper;
import com.hp.biz.logger.model.InvocationInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hp
 */
@Slf4j
public class BizLoggerContext {
    // 元数据不应被客户数据覆盖
    private static final Map<String, Object> metaContextHolder = new ConcurrentHashMap<>(64);
    // 应对线程池化的场景, 方法级别
    private static final TransmittableThreadLocal<Deque<Map<String, Object>>> methodBasedVariableHolder = new TransmittableThreadLocal<>();
    // 应对线程池化的场景, 线程级别
    private static final TransmittableThreadLocal<Map<String, Object>> threadBasedVariableHolder = new TransmittableThreadLocal<>();

    private static final String TENANT_KEY = "_tenant";
    private static final String INVOCATION_INFO_KEY = "_invocation_info";
    public static final String DIFF_OBJECTS_KEY = "_diff_objects";

    public static void addEmptyFrame() {
        Optional.ofNullable(methodBasedVariableHolder.get())
                .ifPresentOrElse(
                        deque -> deque.push(Maps.newHashMap()),
                        () -> {
                            Deque<Map<String, Object>> deque = Queues.newArrayDeque();
                            deque.push(Maps.newHashMap());
                            methodBasedVariableHolder.set(deque);
                        });
    }

    public static void putVariable(String key, Object value) {
        Optional.ofNullable(methodBasedVariableHolder.get())
                .ifPresentOrElse(
                        deque -> {
                            if (deque.isEmpty()) {
                                deque.push(Maps.newHashMap());
                            }
                            deque.element().put(key, value);
                        },
                        () -> {
                            Deque<Map<String, Object>> deque = Queues.newArrayDeque();
                            deque.push(Maps.newHashMap());
                            methodBasedVariableHolder.set(deque);
                            deque.element().put(key, value);
                        });
    }

    public static void putGlobalVariable(String key, Object value) {
        Optional.ofNullable(threadBasedVariableHolder.get())
                .ifPresentOrElse(
                        map -> map.put(key, value),
                        () -> {
                            final Map<String, Object> map = Maps.newConcurrentMap();
                            threadBasedVariableHolder.set(map);
                            map.put(key, value);
                        });
    }

    public static boolean notEmpty() {
        return MapUtil.isNotEmpty(metaContextHolder) ||
                MapUtil.isNotEmpty(threadBasedVariableHolder.get()) ||
                (Objects.nonNull(methodBasedVariableHolder.get()) && MapUtil.isNotEmpty(methodBasedVariableHolder.get().peek()));
    }

    public static Map<String, Object> getAllVariables() {
        final Map<String, Object> variables = Maps.newHashMap(getGlobalVariables());
        variables.putAll(getVariables());
        variables.putAll(metaContextHolder);
        return variables;
    }

    public static Map<String, Object> getVariables() {
        return Optional.ofNullable(methodBasedVariableHolder.get()).map(Deque::peek).orElse(Maps.newHashMap());
    }

    public static Object getVariable(String name) {
        return getVariables().getOrDefault(name, null);
    }

    public static Map<String, Object> getGlobalVariables() {
        return Optional.ofNullable(threadBasedVariableHolder.get()).map(MapUtil::unmodifiable).orElse(Maps.newHashMap());
    }

    public static Object getGlobalVariable(String name) {
        return getGlobalVariables().getOrDefault(name, null);
    }

    public static Object getVariableOrGlobal(String name) {
        return Optional.ofNullable(getVariable(name))
                .orElse(getGlobalVariable(name));
    }

    public static void clear() {
        Optional.ofNullable(methodBasedVariableHolder.get()).ifPresent(Deque::pop);
    }

    public static void clearGlobal() {
        Optional.ofNullable(threadBasedVariableHolder.get()).ifPresent(Map::clear);
    }

    public static void putTenant(String tenant) {
        Preconditions.checkArgument(StrUtil.isNotEmpty(tenant), "Biz logger tenant cant be empty.");
        metaContextHolder.put(TENANT_KEY, tenant);
        log.info("Biz logger tenant has been set as [{}].", tenant);
    }

    public static String getTenant() {
        return (String) metaContextHolder.getOrDefault(TENANT_KEY, null);
    }

    public static void putReturnValue(Object object) {
        final String returnValueKey = SpringUtil.getBean(BizLoggerProperties.class).getReturnValueKey();
        putVariable(returnValueKey, object);
        log.debug("The method invocation return value has been set as {}", object);
    }

    public static Object getReturnValue() {
        final String returnValueKey = SpringUtil.getBean(BizLoggerProperties.class).getReturnValueKey();
        return getVariable(returnValueKey);
    }

    public static void putThrowable(Throwable throwable) {
        final String throwableKey = SpringUtil.getBean(BizLoggerProperties.class).getThrowableKey();
        putVariable(throwableKey, throwable);
        log.debug("The method invocation has thrown an exception of {}.", throwable.getMessage());
    }

    public static Throwable getThrowable() {
        final String throwableKey = SpringUtil.getBean(BizLoggerProperties.class).getThrowableKey();
        return (Throwable) getVariable(throwableKey);
    }

    public static void putInvocationInfo(InvocationInfo invocationInfo) {
        Preconditions.checkArgument(Objects.nonNull(invocationInfo), "The invocationInfo cant be null.");
        putVariable(INVOCATION_INFO_KEY, invocationInfo);
    }

    public static InvocationInfo getInvocationInfo() {
        return (InvocationInfo) getVariable(INVOCATION_INFO_KEY);
    }

    public static void putDiffObjects(Object init, Object changed) {
        putVariable(DIFF_OBJECTS_KEY, new BizDiffObjectWrapper(init, changed));
    }

    public static BizDiffObjectWrapper getDiffObjects() {
        return (BizDiffObjectWrapper) getVariable(DIFF_OBJECTS_KEY);
    }
}
