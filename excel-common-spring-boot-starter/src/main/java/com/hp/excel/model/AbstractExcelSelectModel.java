package com.hp.excel.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.common.base.utils.SpELHelper;
import com.hp.excel.annotation.ExcelOptions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * @author hp
 * @date 2022/11/7
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractExcelSelectModel<T> {

    protected int headLayerCount;

    protected T options;

    protected String columnName;

    protected int columnIndex;

    protected String parentColumnName;

    protected int parentColumnIndex;

    protected int firstRow;

    protected int lastRow;

    public AbstractExcelSelectModel(@Nonnull Field field, @Nonnull ExcelSelect excelSelect, @Nullable ExcelProperty excelProperty, int defaultSort, @Nullable Map<String, Object> parameters) {
        final Optional<ExcelProperty> excelPropertyOpt = Optional.ofNullable(excelProperty);
        this.headLayerCount = excelPropertyOpt.map(property -> property.value().length).orElse(1);
        this.firstRow = Math.max(excelSelect.firstRow(), this.headLayerCount);
        this.lastRow = excelSelect.lastRow();

        this.parentColumnName = excelSelect.parentColumnName();
        this.columnName = excelPropertyOpt.map(property -> property.value()[this.headLayerCount - 1]).orElse(field.getName());
        this.columnIndex = excelPropertyOpt.map(property -> property.index() > -1 ? property.index() : defaultSort).orElse(defaultSort);

        this.options = resolveOptions(excelSelect, parameters);
    }

    public boolean hasParentColumn() {
        return StrUtil.isNotEmpty(this.parentColumnName);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected T resolveOptions(@Nonnull ExcelSelect excelSelect, @Nullable Map<String, Object> parameters) {
        final ExcelOptions excelOptions = excelSelect.options();
        if (StrUtil.isEmpty(excelOptions.expression())) {
            log.warn("The ExcelSelect on {} has no options whatsoever.", this.columnName);
            return null;
        }
        final SpELHelper spELHelper = SpringUtil.getBean(SpELHelper.class);
        return (T) spELHelper.newGetterInstance(excelOptions.expression()).apply(
                null,
                (evaluationContext -> Optional.ofNullable(parameters).ifPresent(map -> map.forEach(evaluationContext::setVariable)))
        );
    }
}
