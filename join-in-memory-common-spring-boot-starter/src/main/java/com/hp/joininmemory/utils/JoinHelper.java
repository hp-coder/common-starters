package com.hp.joininmemory.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import com.hp.common.base.utils.ProgrammaticHelper;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.context.JoinContext;
import com.hp.joininmemory.context.JoinDynamicFieldContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public class JoinHelper {

    public static <DATA> JoinContext<DATA> createJoinContext(Class<DATA> clazz) {
        final JoinInMemoryConfig joinInMemoryConfig = clazz.getAnnotation(JoinInMemoryConfig.class);
        return new JoinContext<>(clazz, joinInMemoryConfig);
    }

    public static void clearJoinContext() {
        JoinContext.clean();
    }

    public static <JOIN_KEY> List<List<JOIN_KEY>> batchJoinKeys(Collection<JOIN_KEY> joinKeys, int batchSize) {
        if (batchSize <= 0) {
            return CollUtil.split(joinKeys, joinKeys.size());
        }
        return CollUtil.split(joinKeys, batchSize);
    }

    /**
     * 0 or less will be ignored
     *
     * @return batch size
     */
    public static int getJoinKeyBatch() {
        return Optional.ofNullable(JoinContext.get())
                .map(JoinContext::getConfig)
                .map(JoinInMemoryConfig::joinBatchSize)
                .orElse(-1);
    }

    @SafeVarargs
    public static <CLZ> void includeFields(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        JoinDynamicFieldContext.addIncludeFields(extractClassName(field), extractFieldNames(field, fields));
    }

    @SafeVarargs
    public static <CLZ> void excludeFields(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        JoinDynamicFieldContext.addExcludeFields(extractClassName(field), extractFieldNames(field, fields));
    }

    public static void clearDynamicFields() {
        JoinDynamicFieldContext.cleanIncludeFields();
        JoinDynamicFieldContext.cleanExcludeFields();
    }

    public static boolean isIncluded(String className, String fieldName) {
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

    public static boolean notExcluded(String className, String fieldName) {
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

    private static <CLZ> String extractClassName(Func1<CLZ, ?> field) {
        return LambdaUtil.getRealClass(field).getSimpleName();
    }

    @SafeVarargs
    private static <CLZ> Set<String> extractFieldNames(Func1<CLZ, ?> field, Func1<CLZ, ?>... fields) {
        final List<Func1<CLZ, ?>> safeFields = ProgrammaticHelper.getSafeVarargs(field, fields);

        return safeFields.stream()
                .map(LambdaUtil::getFieldName)
                .collect(Collectors.toSet());
    }
}
