package com.hp.excel.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.hp.excel.dto.ExcelSelectDataColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.Map;

/**
 * @author HP
 * @date 2022/11/7
 */
public class ExcelUtil {

    public static void addCascadeValidationToSheet(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder, Map<String, List<String>> options, int parentColumnIndex, int childColumnIndex, int fromRow, int endRow) {
        final Workbook workbook = writeWorkbookHolder.getWorkbook();
        final Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        String hiddenSheetName = "sheet" + workbook.getNumberOfSheets();
        Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);
        int rowIndex = 0;
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            String parent = formatNameManager(entry.getKey());
            List<String> children = entry.getValue();
            if (CollUtil.isEmpty(children)) {
                continue;
            }
            int columnIndex = 0;
            Row row = hiddenSheet.createRow(rowIndex++);
            Cell cell = null;
            for (String child : children) {
                cell = row.createCell(columnIndex++);
                cell.setCellValue(child);
            }
            char lastChildrenColumn = (char) ((int) 'A' + (children.size() == 0 ? 1 : children.size()) - 1);
            createNameManager(workbook, parent, String.format(hiddenSheetName + "!$A$%s:$%s$%s", rowIndex, lastChildrenColumn, rowIndex));
            final DataValidationConstraint formulaListConstraint = helper.createFormulaListConstraint("INDIRECT($" + (char) ((int) 'A' + parentColumnIndex) + "2)");
            CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, childColumnIndex, childColumnIndex);
            final DataValidation validation = helper.createValidation(formulaListConstraint, regions);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.setShowErrorBox(true);
            validation.setSuppressDropDownArrow(true);
            validation.createErrorBox("提示", "请输入下拉选项中的内容");
            sheet.addValidationData(validation);
        }
        hideSheet(workbook,1);
    }

    private static Name createNameManager(Workbook workbook, String nameName, String formula) {
        Name name = workbook.createName();
        name.setNameName(nameName);
        name.setRefersToFormula(formula);
        return name;
    }

    private static void hideSheet(Workbook workbook, int start) {
        for (int i = start; i < workbook.getNumberOfSheets(); i++) {
            workbook.setSheetHidden(i, true);
        }
    }

    private static String formatNameManager(String name) {
        name = name.replaceAll(" ", "").replaceAll("-", "_").replaceAll(":", ".");
        if (Character.isDigit(name.charAt(0))) {
            name = "_" + name;
        }
        return name;
    }

    public static void addSelectValidationToSheet(Sheet sheet, DataValidationHelper helper, Integer rowIndex, ExcelSelectDataColumn excelSelectDataColumn) {
        CellRangeAddressList rangeList = new CellRangeAddressList(excelSelectDataColumn.getFirstRow(), excelSelectDataColumn.getLastRow(), rowIndex, rowIndex);
        final List<String> source = (List<String>) excelSelectDataColumn.getSource();
        DataValidationConstraint constraint;
        // 设置下拉列表的值
        final String[] arr = source.toArray(new String[0]);
        constraint = helper.createExplicitListConstraint(arr);
        // 设置约束
        DataValidation validation = helper.createValidation(constraint, rangeList);
        // 阻止输入非下拉选项的值
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("提示", "请输入下拉选项中的内容");
        sheet.addValidationData(validation);
    }
}
