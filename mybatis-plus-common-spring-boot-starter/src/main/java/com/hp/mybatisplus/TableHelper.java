package com.hp.mybatisplus;

import cn.hutool.core.lang.Singleton;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.Maps;
import com.hp.mybatisplus.converter.TypeHandlerAdapter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class TableHelper {

    private static final Map<Class<?>, Map<String, TableFieldCache>> COLUMN_CACHE = Maps.newConcurrentMap();

    public static <AGG_ROOT, FIELD, COLUMN> Optional<TypeHandlerAdapter<FIELD, COLUMN>> columnConverter(@NotNull SFunction<AGG_ROOT, ?> fieldFunction) {
        return Optional.ofNullable(fromCache(fieldFunction).getTypeHandler());
    }

    public static <AGG_ROOT> String columnName(@NotNull SFunction<AGG_ROOT, ?> fieldFunction) {
        return fromCache(fieldFunction).column();
    }

    private static <AGG_ROOT> TableFieldCache fromCache(@NotNull SFunction<AGG_ROOT, ?> fieldFunction) {
        final LambdaMeta meta = LambdaUtils.extract(fieldFunction);
        final Class<?> table = meta.getInstantiatedClass();
        final String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
        return fromL1Cache(meta, fieldName).orElseGet(() -> installL2Cache(meta, fieldName, columnNameFallback(table, fieldName), getTypeHandlerAdapter(table, fieldName)));
    }

    /**
     * 一级缓存, 可查 TableField == null || TableField.exist == true 的列
     */
    private static Optional<TableFieldCache> fromL1Cache(@NotNull LambdaMeta meta, @NotNull String fieldName) {
        final Class<?> table = meta.getInstantiatedClass();
        final Optional<TableFieldInfo> tableFieldInfo = getTableFieldInfo(table, fieldName);
        return tableFieldInfo.map(fieldInfo -> new TableFieldCache(fieldInfo.getColumn(), getTypeHandlerAdapter(fieldInfo.getTypeHandler())))
                .or(() -> fromL2Cache(meta, fieldName));
    }

    /**
     * 二级缓存, 可查 TableField != null && TableField.exist == false 的列
     */
    private static Optional<TableFieldCache> fromL2Cache(@NotNull LambdaMeta meta, @NotNull String fieldName) {
        final Class<?> table = meta.getInstantiatedClass();
        if (!COLUMN_CACHE.containsKey(table)) {
            return Optional.empty();
        }
        final Map<String, TableFieldCache> fieldCacheMap = COLUMN_CACHE.get(table);
        if (!fieldCacheMap.containsKey(fieldName)) {
            return Optional.empty();
        }
        return Optional.of(fieldCacheMap.get(fieldName));
    }

    /**
     * 使用客户端配置规则直接处理这个属性
     */
    public static String columnNameFallback(@NotNull Class<?> table, String fieldName) {
        String column = fieldName;
        final Optional<TableInfo> tableInfo = getTableInfo(table);
        if (tableInfo.isEmpty()) {
            return column;
        }
        if (tableInfo.get().isUnderCamel()) {
            column = StringUtils.camelToUnderline(column);
        }
        final GlobalConfig.DbConfig dbConfig = GlobalConfigUtils.getDbConfig(tableInfo.get().getConfiguration());
        if (dbConfig.isCapitalMode()) {
            /* 开启字段全大写申明 */
            column = column.toUpperCase();
        }
        return column;
    }

    public static Optional<TableInfo> getTableInfo(@NotNull Class<?> table) {
        return Optional.ofNullable(TableInfoHelper.getTableInfo(table));
    }

    public static Optional<TableFieldInfo> getTableFieldInfo(@NotNull Class<?> table, @NotNull Field field) {
        return getTableFieldInfo(table, field.getName());
    }

    public static Optional<TableFieldInfo> getTableFieldInfo(@NotNull Class<?> table, @NotNull String fieldName) {
        return getTableInfo(table)
                .flatMap(
                        tableInfo -> tableInfo.getFieldList()
                                .stream()
                                .filter(tableFieldInfo -> Objects.equals(tableFieldInfo.getProperty(), fieldName))
                                .findFirst()
                );
    }

    @SuppressWarnings({"unchecked"})
    private static Class<? extends TypeHandlerAdapter<?, ?>> getTypeHandlerAdapter(Class<?> table, String fieldName) {
        final Field field;
        try {
            field = table.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Missing field=%s on class=%s".formatted(fieldName, table.getName()), e);
        }
        if (!field.isAnnotationPresent(TableField.class)) {
            return null;
        }
        final Class<? extends TypeHandler<?>> nativeTypeHandlerClass = (Class<? extends TypeHandler<?>>) field.getAnnotation(TableField.class).typeHandler();
        return getTypeHandlerAdapter(nativeTypeHandlerClass);
    }

    @SuppressWarnings({"unchecked"})
    private static Class<? extends TypeHandlerAdapter<?, ?>> getTypeHandlerAdapter(Class<? extends TypeHandler<?>> typeHandler) {
        if (typeHandler == null) {
            return null;
        }
        if (typeHandler == UnknownTypeHandler.class) {
            return null;
        }
        if (!TypeHandlerAdapter.class.isAssignableFrom(typeHandler)) {
            return null;
        }
        return ((Class<? extends TypeHandlerAdapter<?, ?>>) typeHandler);
    }

    private static TableFieldCache installL2Cache(@NotNull LambdaMeta meta, @NotNull String fieldName, @NotNull String columnName, @Nullable Class<? extends TypeHandler<?>> typeHandler) {
        final Class<?> table = meta.getInstantiatedClass();
        final TableFieldCache cache = new TableFieldCache(columnName, getTypeHandlerAdapter(typeHandler));
        COLUMN_CACHE.compute(table, (key, fieldCacheMap) -> {
            if (Objects.isNull(fieldCacheMap)) {
                final Map<String, TableFieldCache> ftc = Maps.newConcurrentMap();
                ftc.put(fieldName, cache);
                return ftc;
            }
            fieldCacheMap.put(fieldName, cache);
            return fieldCacheMap;
        });
        return cache;
    }

    private record TableFieldCache(String column, Class<? extends TypeHandlerAdapter<?, ?>> typeHandlerClass) {
        @SuppressWarnings("unchecked")
        public <T, R> TypeHandlerAdapter<T, R> getTypeHandler() {
            return Optional.ofNullable(typeHandlerClass).map(klass -> (TypeHandlerAdapter<T, R>) Singleton.get(klass)).orElse(null);
        }
    }
}

