package com.hp.excel.enhence.handler;

import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.hp.excel.annotation.ExcelMerge;
import com.hp.excel.enums.MergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 基于内容的动态自定义合并行处理器 *
 *
 * @author hp
 * @date 2022/11/8
 */
@Slf4j
public class ContentBasedDynamicMergeHandler extends DynamicMergeHandler {

    private final Map<Integer, String> rowDataHolder = new HashMap<>();

    private Integer rowIndex = 1;
    private int mergeCount = 0;

    public ContentBasedDynamicMergeHandler(Class<?> dataClass) {
        super(dataClass);
    }

    @Override
    protected void merge(Map<Integer, ExcelMerge> mergeHolder, RowWriteHandlerContext context) {
        final Row row = context.getRow();
        final int physicalNumberOfCells = row.getPhysicalNumberOfCells();
        boolean validMerge = true;
        for (int i = 0; i < physicalNumberOfCells; i++) {
            final Cell cell = row.getCell(i);
            final int columnIndex = cell.getColumnIndex();
            final ExcelMerge excelMerge = mergeHolder.get(columnIndex);
            if (excelMerge == null || !MergeStrategy.CONTENT.equals(excelMerge.rowStrategy())) {
                continue;
            }
            validMerge &= Objects.equals(rowDataHolder.get(columnIndex), cell.getStringCellValue());
            rowDataHolder.put(cell.getColumnIndex(), cell.getStringCellValue());
        }
        if (validMerge) {
            mergeCount++;
        } else {
            if (mergeCount > 0) {
                rowDataHolder.keySet().forEach(columnIndex -> {
                    final CellRangeAddress cellAddresses =
                            new CellRangeAddress(rowIndex, rowIndex + mergeCount, columnIndex, columnIndex);
                    context.getWriteSheetHolder().getSheet().addMergedRegionUnsafe(cellAddresses);
                });
                mergeCount = 0;
            }
            rowIndex = context.getRowIndex();
        }
    }
}
