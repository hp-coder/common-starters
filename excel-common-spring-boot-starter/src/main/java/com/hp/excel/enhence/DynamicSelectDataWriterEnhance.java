package com.hp.excel.enhence;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.google.common.collect.Maps;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.dto.ExcelSelectDataColumn;
import com.hp.excel.head.HeadGenerator;
import com.hp.excel.enhence.handler.SelectDataSheetWriteHandler;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HP
 */
public class DynamicSelectDataWriterEnhance implements ExcelWriterBuilderEnhance {

    @Override
    public ExcelWriterBuilder enhanceExcel(ExcelWriterBuilder writerBuilder, HttpServletResponse response, ResponseExcel responseExcel, Class<?> dataClass, String templatePath) {
        Map<Integer, ExcelSelectDataColumn> selectedMap = resolveExcelSelect(dataClass);
        return writerBuilder.registerWriteHandler(new SelectDataSheetWriteHandler(selectedMap));
    }

    @Override
    public ExcelWriterSheetBuilder enhanceSheet(ExcelWriterSheetBuilder writerSheetBuilder, Integer sheetNo, String sheetName, Class<?> dataClass, String template, Class<? extends HeadGenerator> headEnhancerClass) {
        return writerSheetBuilder;
    }

    private static <T> Map<Integer, ExcelSelectDataColumn> resolveExcelSelect(Class<T> dataClass) {
        Map<Integer, ExcelSelectDataColumn> selectedMap = Maps.newHashMap();
        final Field[] fields = ReflectUtil.getFields(dataClass,
                field -> !field.isAnnotationPresent(ExcelIgnore.class) && !Modifier.isStatic(field.getModifiers())
        );
        AtomicInteger annotatedIndex = new AtomicInteger(0);
        AtomicInteger maxHeadLayers = new AtomicInteger(1);
        Arrays.stream(fields)
                .forEach(f -> {
                    ExcelSelect selected = f.getAnnotation(ExcelSelect.class);
                    ExcelProperty property = f.getAnnotation(ExcelProperty.class);
                    final int index = annotatedIndex.getAndIncrement();
                    if (selected != null) {
                        ExcelSelectDataColumn excelSelectedResolve;
                        if (StrUtil.isNotEmpty(selected.parentColumn())) {
                            excelSelectedResolve = new ExcelSelectDataColumn<Map<String, List<String>>>();
                        } else {
                            excelSelectedResolve = new ExcelSelectDataColumn<List<String>>();
                        }
                        final Object source = excelSelectedResolve.resolveSource(selected);
                        final int headLayerCount = property != null ? property.value().length : 1;
                        final String columName = property != null ? property.value()[headLayerCount - 1] : f.getName();
                        maxHeadLayers.set(Math.max(headLayerCount, maxHeadLayers.get()));
                        excelSelectedResolve.setParentColumn(selected.parentColumn());
                        excelSelectedResolve.setColumn(columName);
                        excelSelectedResolve.setSource(Objects.nonNull(source) ? source : Collections.emptyList());
                        excelSelectedResolve.setFirstRow(Math.max(selected.firstRow(), headLayerCount));
                        excelSelectedResolve.setLastRow(selected.lastRow());
                        excelSelectedResolve.setColumnIndex(index);
                        selectedMap.put(index, excelSelectedResolve);
                    }
                });

        if (CollUtil.isNotEmpty(selectedMap)) {
            selectedMap.forEach((k, v) -> {
                v.setFirstRow(Math.max(v.getFirstRow(), maxHeadLayers.get()));
            });
            final Map<String, Integer> indexMap = selectedMap
                    .values()
                    .stream()
                    .collect(Collectors.toMap(ExcelSelectDataColumn::getColumn, ExcelSelectDataColumn::getColumnIndex));
            selectedMap.forEach((k, v) -> {
                if (indexMap.containsKey(v.getParentColumn())) {
                    v.setParentColumnIndex(indexMap.get(v.getParentColumn()));
                }
            });
        }
        return selectedMap;
    }
}
