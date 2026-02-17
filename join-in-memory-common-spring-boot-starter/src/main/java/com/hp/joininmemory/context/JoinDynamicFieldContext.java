package com.hp.joininmemory.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * 操作动态Join字段
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/7
 */
public class JoinDynamicFieldContext {

    private static final TransmittableThreadLocal<Map<String, Set<String>>> INCLUDE_JOIN_FIELDS_THREAD_LOCAL = TransmittableThreadLocal.withInitial(Maps::newHashMap);

    private static final TransmittableThreadLocal<Map<String, Set<String>>> EXCLUDE_JOIN_FIELDS_THREAD_LOCAL = TransmittableThreadLocal.withInitial(Maps::newHashMap);

    public static Map<String, Set<String>> getIncludeFields() {
        return INCLUDE_JOIN_FIELDS_THREAD_LOCAL.get();
    }

    public static Map<String, Set<String>> getExcludeFields() {
        return EXCLUDE_JOIN_FIELDS_THREAD_LOCAL.get();
    }

    public static Set<String> getIncludeFields(String className) {
        return INCLUDE_JOIN_FIELDS_THREAD_LOCAL.get().getOrDefault(className, Sets.newHashSet());
    }

    public static Set<String> getExcludeFields(String className) {
        return EXCLUDE_JOIN_FIELDS_THREAD_LOCAL.get().getOrDefault(className, Sets.newHashSet());
    }

    public static void addIncludeFields(String className, Set<String> includeFields) {
        final Map<String, Set<String>> classFieldMap = INCLUDE_JOIN_FIELDS_THREAD_LOCAL.get();
        Set<String> fields = classFieldMap.computeIfAbsent(className, k -> Sets.newHashSet());
        fields.addAll(includeFields);
    }

    public static void addExcludeFields(String className, Set<String> excludeFields) {
        final Map<String, Set<String>> classFieldMap = EXCLUDE_JOIN_FIELDS_THREAD_LOCAL.get();
        Set<String> fields = classFieldMap.computeIfAbsent(className, k -> Sets.newHashSet());
        fields.addAll(excludeFields);
    }

    public static void cleanIncludeFields() {
        INCLUDE_JOIN_FIELDS_THREAD_LOCAL.remove();
    }

    public static void cleanExcludeFields() {
        EXCLUDE_JOIN_FIELDS_THREAD_LOCAL.remove();
    }
}
