package com.hp.excel.example.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.example.select_data_handler.DynamicSelectDataHandler;
import com.hp.excel.example.select_data_handler.DynamicSelectPrimaryHandler;
import com.hp.excel.example.select_data_handler.DynamicSelectSecondaryHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HP 2023/1/5
 */
@ColumnWidth(50)
@Data
public class ExcelExample implements Serializable {

    @ExcelProperty("普通列")
    private String regularColumn;

    @ExcelSelect(staticData = {"静态1", "静态2", "静态3"})
    @ExcelProperty("静态单列下拉列表")
    private String staticSelectColumn;

    @ExcelSelect(handler = DynamicSelectDataHandler.class)
    @ExcelProperty("动态单列下拉列表")
    private String dynamicSelectColumn;

    @ExcelSelect(handler = DynamicSelectPrimaryHandler.class)
    @ExcelProperty("级联下拉列表第一级")
    private String dynamicSelectPrimaryColumn;

    @ExcelSelect(parentColumn = "级联下拉列表第一级", handler = DynamicSelectSecondaryHandler.class)
    @ExcelProperty("级联下拉列表第二级")
    private String dynamicSelectSecondaryColumn;

    private static final long serialVersionUID = -8498694378786074852L;
}
