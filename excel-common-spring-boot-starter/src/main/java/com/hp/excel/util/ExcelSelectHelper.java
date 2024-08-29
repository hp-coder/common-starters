package com.hp.excel.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hp.excel.model.ExcelCascadeModel;
import com.hp.excel.model.ExcelSelectModel;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.model.AbstractExcelSelectModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class ExcelSelectHelper {

    @Nullable
    public static <T> Map<Integer, ? extends AbstractExcelSelectModel<?>> createSelectionMapping(@Nonnull Class<T> dataClass) {
        return createSelectionMapping(dataClass, null);
    }

    @Nullable
    public static <T> Map<Integer, ? extends AbstractExcelSelectModel<?>> createSelectionMapping(@Nonnull Class<T> dataClass, @Nullable Map<String, Object> parameters) {
        final Field[] fields = ReflectUtil.getFields(dataClass);
        final AtomicInteger fieldIndex = new AtomicInteger(0);
        final Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping = Arrays.stream(fields)
                .map(field -> {
                    final ExcelSelect excelSelect = AnnotatedElementUtils.getMergedAnnotation(field, ExcelSelect.class);
                    if (Objects.isNull(excelSelect)) {
                        log.debug("No ExcelSelect annotated on {}, skip processing", field.getName());
                        fieldIndex.getAndIncrement();
                        return null;
                    }
                    final ExcelProperty excelProperty = AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class);
                    AbstractExcelSelectModel<?> excelSelectModel;
                    if (StrUtil.isNotEmpty(excelSelect.parentColumnName())) {
                        excelSelectModel = new ExcelCascadeModel(field, excelSelect, excelProperty, fieldIndex.getAndIncrement(), parameters);
                    } else {
                        excelSelectModel = new ExcelSelectModel(field, excelSelect, excelProperty, fieldIndex.getAndIncrement(), parameters);
                    }
                    return excelSelectModel;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AbstractExcelSelectModel::getColumnIndex, Function.identity(), (a, b) -> a));

        if (MapUtil.isEmpty(selectionMapping)) {
            return null;
        }

        // 设置父列索引
        final Map<String, Integer> columnNamedMapping = selectionMapping.values()
                .stream()
                .collect(Collectors.toMap(AbstractExcelSelectModel::getColumnName, AbstractExcelSelectModel::getColumnIndex));
        selectionMapping.forEach((k, v) -> {
            if (v.hasParentColumn() && columnNamedMapping.containsKey(v.getParentColumnName())) {
                v.setParentColumnIndex(columnNamedMapping.get(v.getParentColumnName()));
            }
        });

        return selectionMapping;
    }
}
