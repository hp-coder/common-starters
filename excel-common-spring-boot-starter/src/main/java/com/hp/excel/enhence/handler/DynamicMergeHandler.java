package com.hp.excel.enhence.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.hp.excel.annotation.ExcelMerge;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HP
 * @date 2022/11/8
 */
@Slf4j
public abstract class DynamicMergeHandler implements RowWriteHandler {

    private final Class<?> dataClass;
    private final Map<Integer, ExcelMerge> mergeHolder;

    public DynamicMergeHandler(Class<?> dataClass) {
        this.dataClass = dataClass;
        this.mergeHolder = this.init(dataClass);
    }

    protected Map<Integer, ExcelMerge> init(Class<?> dataClass) {
        Map<Integer, ExcelMerge> mergeHolder = new HashMap<>();
        List<Field> fieldHolder = new ArrayList<>();
        for (Class<?> acls = dataClass; acls != null; acls = acls.getSuperclass()) {
            final List<Field> fields = Arrays.stream(acls.getDeclaredFields())
                    .peek(f -> {
                        if (!Modifier.isPublic(f.getModifiers())) {
                            f.setAccessible(true);
                        }
                    })
                    .filter(f -> f.isAnnotationPresent(ExcelProperty.class))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fields)) {
                fieldHolder.addAll(fields);
            }
        }
        if (CollUtil.isNotEmpty(fieldHolder)) {
            for (int i = 0; i < fieldHolder.size(); i++) {
                final Field field = fieldHolder.get(i);
                final ExcelMerge excelMerge = field.getAnnotation(ExcelMerge.class);
                if (excelMerge != null && excelMerge.mergeRow()) {
                    final ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    if (excelProperty == null) {
                        throw new UnsupportedOperationException(" @ExcelMerge works only when @ExcelProperty is set ");
                    }
                    final int index = excelProperty.index() != -1 ? excelProperty.index() : i;
                    mergeHolder.put(index, excelMerge);
                }
            }
        }
        return mergeHolder;
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getHead() || context.getRelativeRowIndex() == null) {
            return;
        }
        this.merge(mergeHolder, context);
    }

    protected abstract void merge(Map<Integer, ExcelMerge> mergeHolder, RowWriteHandlerContext context);
}
