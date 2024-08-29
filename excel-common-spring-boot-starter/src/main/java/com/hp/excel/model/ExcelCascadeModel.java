package com.hp.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hp.excel.annotation.ExcelSelect;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author hp
 * @date 2022/11/7
 */
@EqualsAndHashCode(callSuper = false)
public class ExcelCascadeModel extends AbstractExcelSelectModel<Map<Object, Collection<Object>>> {

    public ExcelCascadeModel(@NotNull Field field, @NotNull ExcelSelect excelSelect, @Nullable ExcelProperty excelProperty, int defaultSort, @Nullable Map<String, Object> parameters) {
        super(field, excelSelect, excelProperty, defaultSort, parameters);
    }
}
