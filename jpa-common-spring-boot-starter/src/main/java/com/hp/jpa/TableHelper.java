package com.hp.jpa;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import com.google.common.collect.Maps;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * <a href="https://github.com/actframework/actframework">actframework</a>
 *
 * @author hp
 */
@Slf4j
@UtilityClass
public class TableHelper {

    private static final Map<Class<?>, Map<String, String>> COLUMN_CACHE = Maps.newConcurrentMap();

    public static <T> String columnName(@NotNull Func1<T, ?> fieldFunction) {
        final String fieldName = LambdaUtil.getFieldName(fieldFunction);
        final Class<?> dataClass = LambdaUtil.getRealClass(fieldFunction);
        if (COLUMN_CACHE.containsKey(dataClass)) {
            final Map<String, String> fieldToColumn = COLUMN_CACHE.get(dataClass);
            if (fieldToColumn.containsKey(fieldName)) {
                return fieldToColumn.get(fieldName);
            }
        }

        installCache(dataClass, fieldName, fieldName);
        return fieldName;
    }

    private static void installCache(@NotNull Class<?> dataClass, @NotNull String fieldName, @NotNull String columnName) {
        COLUMN_CACHE.compute(dataClass, (key, fieldToColumn) -> {
            if (Objects.isNull(fieldToColumn)) {
                final Map<String, String> ftc = Maps.newConcurrentMap();
                ftc.put(fieldName, columnName);
                return ftc;
            }
            fieldToColumn.put(fieldName, columnName);
            return fieldToColumn;
        });
    }
}
