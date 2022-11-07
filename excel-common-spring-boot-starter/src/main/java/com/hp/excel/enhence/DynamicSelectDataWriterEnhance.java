package com.hp.excel.enhence;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.dto.ExcelSelectDataColumn;
import com.hp.excel.enhence.handler.SelectDataSheetWriteHandler;
import com.hp.excel.head.HeadGenerator;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HP
 * @date 2022/11/7
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
        Map<Integer, ExcelSelectDataColumn> selectedMap = new HashMap<>(16);
        Field[] fields = dataClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelSelect selected = field.getAnnotation(ExcelSelect.class);
            ExcelProperty property = field.getAnnotation(ExcelProperty.class);
            if (selected != null) {
                ExcelSelectDataColumn excelSelectedResolve;
                if (StrUtil.isNotEmpty(selected.parentColumn())) {
                    excelSelectedResolve = new ExcelSelectDataColumn<Map<String, List<String>>>();
                } else {
                    excelSelectedResolve = new ExcelSelectDataColumn<List<String>>();
                }
                final Object source = excelSelectedResolve.resolveSource(selected);
                if (Objects.nonNull(source)) {
                    if (property != null) {
                        excelSelectedResolve.setParentColumn(selected.parentColumn());
                        excelSelectedResolve.setColumn(property.value()[0]);
                        excelSelectedResolve.setSource(source);
                        excelSelectedResolve.setFirstRow(selected.firstRow());
                        excelSelectedResolve.setLastRow(selected.lastRow());
                        int index = property.index();
                        if (index >= 0) {
                            selectedMap.put(index, excelSelectedResolve);
                        } else {
                            index = i;
                            selectedMap.put(index, excelSelectedResolve);
                        }
                        excelSelectedResolve.setColumnIndex(index);
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(selectedMap)) {
            final Map<String, Integer> indexMap = selectedMap.values().stream().collect(Collectors.toMap(ExcelSelectDataColumn::getColumn, ExcelSelectDataColumn::getColumnIndex));
            selectedMap.forEach((k, v) -> {
                if (indexMap.containsKey(v.getParentColumn())) {
                    v.setParentColumnIndex(indexMap.get(v.getParentColumn()));
                }
            });
        }
        return selectedMap;
    }
}
