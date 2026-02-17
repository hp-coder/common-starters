package com.hp.joininmemory.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.common.base.utils.ProgrammaticHelper;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.context.JoinContext;
import com.hp.joininmemory.context.JoinDynamicFieldContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for managing in-memory join operations.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/7
 */
@Slf4j
public class JoinHelper {

    /**
     * Creates a root join context for the given class.
     *
     * @param clazz  the class for which to create the join context
     * @param <DATA> the type of data
     * @return the created join context
     */
    public static <DATA> JoinContext<DATA> createRootJoinContext(Class<DATA> clazz) {
        final JoinInMemoryConfig joinInMemoryConfig = clazz.getAnnotation(JoinInMemoryConfig.class);
        return JoinContext.createRootJoinContext(clazz, joinInMemoryConfig);
    }

    /**
     * Clears the current root join context.
     */
    public static void clearRootJoinContext() {
        JoinContext.clean();
        log.debug("Join Context Cleaned");
    }

    /**
     * Splits a collection of join keys into batches of the specified size.
     *
     * @param joinKeys   the collection of join keys to split
     * @param batchSize  the size of each batch
     * @param <JOIN_KEY> the type of join key
     * @return a list of lists, where each inner list is a batch of join keys
     */
    public static <JOIN_KEY> List<List<JOIN_KEY>> batchJoinKeys(Collection<JOIN_KEY> joinKeys, int batchSize) {
        if (batchSize <= 0) {
            return CollUtil.split(joinKeys, joinKeys.size());
        }
        return CollUtil.split(joinKeys, batchSize);
    }

    /**
     * Retrieves the join key batch size from the current join context.
     * A value of 0 or less will be ignored.
     *
     * @return the batch size, or -1 if not configured
     */
    public static int getJoinKeyBatch() {
        return Optional.ofNullable(JoinContext.get())
                .map(JoinContext::getConfig)
                .map(JoinInMemoryConfig::joinBatchSize)
                .orElse(-1);
    }

    /**
     * Includes specific fields for dynamic joining using lambda expressions.
     *
     * @param field  the primary field to include
     * @param fields additional fields to include
     * @param <CLZ>  the type of the class containing the fields
     */
    @SafeVarargs
    public static <CLZ> void includeFields(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        Preconditions.checkArgument(field != null);
        JoinDynamicFieldContext.addIncludeFields(extractClassName(field), extractFieldNames(field, fields));
    }

    /**
     * Includes specific fields for dynamic joining using class name and field names.
     *
     * @param className  the name of the class containing the fields
     * @param fieldNames the names of the fields to include
     */
    public static void includeFields(String className, String... fieldNames) {
        Preconditions.checkArgument(StrUtil.isNotBlank(className));
        Preconditions.checkArgument(ArrayUtil.isNotEmpty(fieldNames));

        JoinDynamicFieldContext.addIncludeFields(className, Set.of(fieldNames));
    }

    /**
     * Excludes specific fields from dynamic joining using lambda expressions.
     *
     * @param field  the primary field to exclude
     * @param fields additional fields to exclude
     * @param <CLZ>  the type of the class containing the fields
     */
    @SafeVarargs
    public static <CLZ> void excludeFields(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        Preconditions.checkArgument(field != null);
        JoinDynamicFieldContext.addExcludeFields(extractClassName(field), extractFieldNames(field, fields));
    }

    /**
     * Excludes specific fields from dynamic joining using class name and field names.
     *
     * @param className  the name of the class containing the fields
     * @param fieldNames the names of the fields to exclude
     */
    public static void excludeFields(String className, String... fieldNames) {
        Preconditions.checkArgument(StrUtil.isNotBlank(className));
        Preconditions.checkArgument(ArrayUtil.isNotEmpty(fieldNames));

        JoinDynamicFieldContext.addExcludeFields(className, Set.of(fieldNames));
    }

    /**
     * Clears all dynamically included and excluded fields.
     */
    public static void clearDynamicFields() {
        log.debug("Clearing Included Join Fields: {}", JoinDynamicFieldContext.getIncludeFields());
        JoinDynamicFieldContext.cleanIncludeFields();

        log.debug("Clearing Excluded Join Fields: {}", JoinDynamicFieldContext.getExcludeFields());
        JoinDynamicFieldContext.cleanExcludeFields();
    }

    /**
     * Checks if a field is valid for joining based on inclusion and exclusion rules.
     *
     * @param className the name of the class containing the field
     * @param fieldName the name of the field to check
     * @return true if the field is valid, false otherwise
     */
    public static boolean isValidField(String className, String fieldName) {
        // fixed order
        return notExcluded(className, fieldName) && isIncluded(className, fieldName);
    }

    /**
     * Checks if a field is included in the dynamic join configuration.
     *
     * @param className the name of the class containing the field
     * @param fieldName the name of the field to check
     * @return true if the field is included, false otherwise
     */
    private static boolean isIncluded(String className, String fieldName) {
        final Set<String> includeFields = JoinDynamicFieldContext.getIncludeFields(className);
        if (CollUtil.isEmpty(includeFields)) {
            return true;
        }
        // Grouped join support
        final String[] fieldNames = fieldName.split(StrUtil.COMMA);
        for (String field : fieldNames) {
            if (includeFields.contains(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a field is not excluded from the dynamic join configuration.
     *
     * @param className the name of the class containing the field
     * @param fieldName the name of the field to check
     * @return true if the field is not excluded, false otherwise
     */
    private static boolean notExcluded(String className, String fieldName) {
        final Set<String> excludeFields = JoinDynamicFieldContext.getExcludeFields(className);
        if (CollUtil.isEmpty(excludeFields)) {
            return true;
        }
        // Grouped join support

        final String[] fieldNames = fieldName.split(StrUtil.COMMA);
        for (String field : fieldNames) {
            if (!excludeFields.contains(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the simple class name from a lambda expression.
     *
     * @param field the lambda expression representing the field
     * @param <CLZ> the type of the class containing the field
     * @return the simple class name
     */
    private static <CLZ> String extractClassName(Func1<CLZ, ?> field) {
        return LambdaUtil.getRealClass(field).getSimpleName();
    }

    /**
     * Extracts field names from lambda expressions.
     *
     * @param field  the primary lambda expression representing the field
     * @param fields additional lambda expressions representing fields
     * @param <CLZ>  the type of the class containing the fields
     * @return a set of field names
     */
    @SafeVarargs
    private static <CLZ> Set<String> extractFieldNames(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        final List<Func1<CLZ, ?>> safeFields = ProgrammaticHelper.getSafeVarargs(field, fields);

        return safeFields.stream()
                .map(LambdaUtil::getFieldName)
                .collect(Collectors.toSet());
    }
}
