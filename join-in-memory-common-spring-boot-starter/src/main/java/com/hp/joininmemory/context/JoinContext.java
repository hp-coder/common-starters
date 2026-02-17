package com.hp.joininmemory.context;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.base.Function;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Join entrypoint context
 * <p>
 * If the user class wasn't annotated with {@link JoinInMemoryConfig}, a default configuration will be used, which is
 * annotated on this class.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Slf4j
@Getter
@JoinInMemoryConfig
public class JoinContext<DATA> {

    /**
     * Deal with pooling problems
     */
    private final static TransmittableThreadLocal<JoinContext<?>> JOIN_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * The join class
     */
    private final Class<DATA> dataClass;

    /**
     * The join config
     * <p>
     * If the join class wasn't annotated with {@link JoinInMemoryConfig}, a default config will be used.
     */
    private final JoinInMemoryConfig config;

    /**
     * The source data path
     * <p>
     * Used in Nested Processing
     */
    private final Deque<Field> pathQueue;

    /**
     * Hierarchical Layers
     */
    private final Deque<Class<?>> hierarchicalQueue;

    private JoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        this.dataClass = dataClass;
        this.pathQueue = new LinkedList<>();
        this.hierarchicalQueue = new LinkedList<>();
        this.hierarchicalQueue.addLast(dataClass);
        this.config = Optional.ofNullable(config).orElse(JoinContext.class.getAnnotation(JoinInMemoryConfig.class));
        JOIN_CONTEXT_THREAD_LOCAL.set(this);
    }

    public static void clean() {
        JOIN_CONTEXT_THREAD_LOCAL.remove();
    }

    @SuppressWarnings("unchecked")
    public static <DATA> JoinContext<DATA> get() {
        return (JoinContext<DATA>) JOIN_CONTEXT_THREAD_LOCAL.get();
    }

    public static <DATA> JoinContext<DATA> createRootJoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        return new JoinContext<>(dataClass, config);
    }

    private static boolean isPrimativeOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || isWrapperType(clazz);
    }

    private static boolean isWrapperType(Class<?> type) {
        return type == Boolean.class || type == Character.class ||
                type == Byte.class || type == Short.class ||
                type == Integer.class || type == Long.class ||
                type == Float.class || type == Double.class ||
                type == Void.class || type == String.class;
    }

    public static boolean invalidFieldType(Class<?>[] clazz, Field field) {
        clazz[0] = field.getType();
        // 如果是基本类型或包装类型，跳过
        if (JoinContext.isPrimativeOrWrapper(clazz[0])) {
            return true;
        }
        // 如果是Collection子类，提取泛型类型
        if (Collection.class.isAssignableFrom(clazz[0])) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    clazz[0] = (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return false;
    }

    /**
     * Record nested path for joining
     */
    public void enter(Field path, Class<?> currentClass) {
        hierarchicalQueue.addLast(currentClass);
        pathQueue.addLast(path);
    }

    /**
     * Exit nested path
     */
    public void exit() {
        // 保留根类
        if (CollUtil.isNotEmpty(hierarchicalQueue) && hierarchicalQueue.size() > 1) {
            hierarchicalQueue.removeLast();
        }
        if (CollUtil.isNotEmpty(pathQueue)) {
            pathQueue.removeLast();
        }
    }

    /**
     * Get current class
     */
    public Class<?> getCurrentClass() {
        return hierarchicalQueue.getLast();
    }

    /**
     * Get current class fields
     */
    public List<Field> getCurrentClassFields() {
        return FieldUtils.getAllFieldsList(getCurrentClass());
    }

    /**
     * Whether is nested field
     */
    public boolean isNestedField() {
        return !pathQueue.isEmpty();
    }

    /**
     * Get the hierarchical path
     */
    public List<String> getHierarchicalPaths() {
        return pathQueue.stream().map(Field::getName).toList();
    }

    /**
     * Get the hierarchical path
     */
    public String getHierarchicalPath(Field field) {
        if (isNestedField()) {
            return String.join(".", getHierarchicalPaths()) + "." + field.getName();
        }
        return field.getName();
    }

    /**
     * Get the run level
     */
    public int getRunLevel(Function<Field, Integer> runLevelExtractor, Integer currentRunLevel) {
        if (isNestedField()) {
            return getHierarchicalRunLevel(runLevelExtractor);
        }
        return currentRunLevel;
    }

    /**
     * Get the hierarchical run level
     */
    private int getHierarchicalRunLevel(Function<Field, Integer> runLevelExtractor) {
        int steps = 0;
        if (isNestedField()) {
            final Field firstLayer = pathQueue.peekFirst();
            steps += runLevelExtractor.apply(firstLayer);
        }
        return steps + pathQueue.size();
    }
}
