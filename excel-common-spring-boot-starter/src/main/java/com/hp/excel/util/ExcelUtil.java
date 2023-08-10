package com.hp.excel.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HP
 */
@UtilityClass
public class ExcelUtil {

    private static final int limitation = 100;

    public static Sheet addCascadeValidationToSheet(
            WriteWorkbookHolder workbookHolder,
            WriteSheetHolder sheetHolder,
            Sheet tmpSheet,
            Map<String, List<String>> options,
            AtomicInteger startCol,
            int parentCol,
            int selfCol,
            int startRow,
            int endRow
    ) {
        final Workbook workbook = workbookHolder.getWorkbook();
        final Sheet sheet = sheetHolder.getSheet();
        tmpSheet = createTmpSheet(tmpSheet, workbook, "cascade_sheet");

        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            String parentVal = formatNameManager(entry.getKey());
            List<String> children = entry.getValue();
            if (CollUtil.isEmpty(children)) {
                continue;
            }

            int columnIndex = startCol.getAndIncrement();
            createDropdownElement(tmpSheet, children, columnIndex);
            if (children.size() >= limitation) {
                tmpSheet = createTmpSheet(null, workbook, "cascade_sheet");
            }

            final String columnName = calculateColumnName(columnIndex + 1);
            final String formula = createFormulaForNameManger(tmpSheet, children.size(), columnName);
            createNameManager(workbook, parentVal, formula);
        }
        final String parentColumnName = calculateColumnName(parentCol + 1);
        final String indirectFormula = createIndirectFormula(parentColumnName, startRow + 1);
        createValidation(workbook, sheet, tmpSheet, indirectFormula, selfCol, startRow, endRow);
        return tmpSheet;
    }


    private static Sheet createTmpSheet(Sheet tmpSheet, Workbook workbook, String sheetName) {
        final String actualName = sheetName + workbook.getNumberOfSheets();
        if (tmpSheet == null) {
            tmpSheet = workbook.createSheet(actualName);
        }
        return tmpSheet;
    }

    public static Sheet addSelectValidationToSheet(
            WriteWorkbookHolder workbookHolder,
            WriteSheetHolder sheetHolder,
            Sheet tmpSheet,
            List<String> options,
            AtomicInteger startCol,
            int selfCol,
            int startRow,
            int endRow
    ) {
        final Workbook workbook = workbookHolder.getWorkbook();
        final Sheet sheet = sheetHolder.getSheet();
        tmpSheet = createTmpSheet(tmpSheet, workbook, "sheet");
        final int columnIndex = startCol.getAndIncrement();
        String columnName = calculateColumnName(columnIndex + 1);
        final String formula = createFormulaForDropdown(tmpSheet, options.size(), columnName);
        createDropdownElement(tmpSheet, options, columnIndex);
        createValidation(workbook, sheet, tmpSheet, formula, selfCol, startRow, endRow);
        return options.size() >= limitation ? null : tmpSheet;
    }


    private static void createDropdownElement(Sheet tmpSheet, List<String> options, int columnIndex) {
        int rowIndex = 0;
        for (String val : options) {
            final int rIndex = rowIndex++;
            final Row row = Optional.ofNullable(tmpSheet.getRow(rIndex))
                    .orElseGet(() -> {
                        try {
                            return tmpSheet.createRow(rIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }

                    });
            final Cell cell = row.createCell(columnIndex);
            cell.setCellValue(val);
        }
    }

    private static void createValidation(Workbook workbook, Sheet sheet, Sheet tmpSheet, String formula, int selfCol, int startRow, int endRow) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        final DataValidationConstraint constraint = helper.createFormulaListConstraint(formula);
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, selfCol, selfCol);
        final DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("提示", "请输入下拉选项中的内容");
        sheet.addValidationData(validation);
        hideSheet(workbook, tmpSheet);
    }

    private static String createIndirectFormula(String columnName, int startRow) {
        final String format = "INDIRECT($%s%s)";
        return String.format(format, columnName, startRow);
    }

    private static String createFormulaForNameManger(Sheet tmpSheet, int size, String columnName) {
        final String format = "%s!$%s$%s:$%s$%s";
        return String.format(format, tmpSheet.getSheetName(), columnName, "1", columnName, size);
    }

    private static String createFormulaForDropdown(Sheet tmpSheet, int size, String columnName) {
        final String format = "=%s!$%s$%s:$%s$%s";
        return String.format(format, tmpSheet.getSheetName(), columnName, "1", columnName, size);
    }

    private static void createNameManager(Workbook workbook, String nameName, String formula) {
        //处理存在名称管理器复用的情况
        Name name = workbook.getName(nameName);
        if (name != null) {
            return;
        }
        name = workbook.createName();
        name.setNameName(nameName);
        name.setRefersToFormula(formula);
    }

    private static void hideSheet(Workbook workbook, Sheet sheet) {
        final int sheetIndex = workbook.getSheetIndex(sheet);
        if (sheetIndex > -1) {
            workbook.setSheetHidden(sheetIndex, true);
        }
    }

    private static String formatNameManager(String name) {
        name = name
                .replaceAll(" ", "")
                .replaceAll("-", "_")
                .replaceAll(":", ".");
        if (Character.isDigit(name.charAt(0))) {
            name = "_" + name;
        }
        return name;
    }

    private static String calculateColumnName(int columnCount) {
        final int minimumExponent = minimumExponent(columnCount);
        final int base = 26, layers = (minimumExponent == 0 ? 1 : minimumExponent);
        final List<Character> sequence = Lists.newArrayList();
        int remain = columnCount;
        for (int i = 0; i < layers; i++) {
            int step = (int) (remain / Math.pow(base, i) % base);
            step = step == 0 ? base : step;
            buildColumnNameSequence(sequence, step);
            remain = remain - step;
        }
        return sequence.stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static void buildColumnNameSequence(List<Character> sequence, int columnIndex) {
        final int capitalAAsIndex = 64;
        sequence.add(0, (char) (capitalAAsIndex + columnIndex));
    }

    private static int minimumExponent(int source) {
        final int base = 26;
        int exponent = 0;
        while (Math.pow(base, exponent) < source) {
            exponent++;
        }
        return exponent;
    }
}
