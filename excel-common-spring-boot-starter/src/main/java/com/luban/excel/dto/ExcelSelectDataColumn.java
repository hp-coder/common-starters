package com.luban.excel.dto;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.luban.excel.annotation.ExcelSelect;
import com.luban.excel.handler.ColumnDynamicSelectDataHandler;
import lombok.Data;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author hp
 * @date 2022/11/7
 */
@Data
public class ExcelSelectDataColumn<T> {

    private T source;

    private String column;

    private int columnIndex;

    private String parentColumn;

    private int parentColumnIndex;

    private int firstRow;

    private int lastRow;

    public T resolveSource(ExcelSelect excelSelect) {
        if (excelSelect == null) {
            return null;
        }
        // 获取固定下拉框的内容
        final String[] staticData = excelSelect.staticData();
        if (ArrayUtil.isNotEmpty(staticData)) {
            return (T) Arrays.asList(staticData);
        }
        // 获取动态下拉框的内容
        final Class<? extends ColumnDynamicSelectDataHandler> handlerClass = excelSelect.handler();
        if (Objects.nonNull(handlerClass)) {
            final ColumnDynamicSelectDataHandler handler = SpringUtil.getBean(handlerClass);
            return (T) handler.source().apply(StrUtil.isNotEmpty(excelSelect.parameter()) ? excelSelect.parameter() : null);
        }
        return null;
    }
}
