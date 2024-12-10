package com.hp.excel.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.constant.ExcelConstants;
import com.hp.excel.context.ExcelContext;
import com.hp.excel.model.ExcelCascadeModel;
import com.hp.excel.model.ExcelSelectModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class ExcelHelper {

    public static void addCascadeDropdownToSheet(
            Workbook workbook,
            Sheet sheet,
            Sheet selectionSheet,
            AtomicInteger selectionColumnIndex,
            ExcelCascadeModel cascadeModel
    ) {
        addCascadeDropdownToSheet(
                workbook,
                sheet,
                selectionSheet,
                selectionColumnIndex,
                cascadeModel.getOptions(),
                cascadeModel.getParentColumnIndex(),
                cascadeModel.getColumnIndex(),
                cascadeModel.getFirstRow(),
                cascadeModel.getLastRow()
        );
    }

    public static void addDropdownToSheet(
            Sheet sheet,
            Sheet selectionSheet,
            AtomicInteger selectionColumnIndex,
            ExcelSelectModel selectModel
    ) {
        addDropdownToSheet(
                sheet,
                selectionSheet,
                selectionColumnIndex,
                selectModel.getOptions(),
                selectModel.getColumnIndex(),
                selectModel.getFirstRow(),
                selectModel.getLastRow()
        );
    }

    private static void addCascadeDropdownToSheet(
            Workbook workbook,
            Sheet sheet,
            Sheet selectionSheet,
            AtomicInteger selectionColumnIndex,
            Map<Object, Collection<Object>> options,
            int parentColumnIndex,
            int columnIndex,
            int startRowIndex,
            int endRowIndex
    ) {
        final String parentColumnName = calculateColumnName(parentColumnIndex + 1);
        final String indirectFormula = createIndirectFormula(parentColumnName, startRowIndex + 1);
        createValidation(sheet, indirectFormula, columnIndex, startRowIndex, endRowIndex);

        options.forEach((parentOption, childOptions) -> {
            if (CollUtil.isEmpty(childOptions)) {
                return;
            }
            final int index = selectionColumnIndex.getAndIncrement();
            createDropdownElement(selectionSheet, childOptions, index);
            final String actualColumnName = calculateColumnName(index + 1);
            final String formulaForNameManger = createFormulaForNameManger(selectionSheet, actualColumnName, childOptions.size());
            createNameManager(workbook, sheet, parentOption, formulaForNameManger);
        });
    }

    private static void addDropdownToSheet(
            Sheet sheet,
            Sheet selectionSheet,
            AtomicInteger selectionColumnIndex,
            Collection<Object> options,
            int columnIndex,
            int startRowIndex,
            int endRowIndex
    ) {
        final int index = selectionColumnIndex.getAndIncrement();
        createDropdownElement(selectionSheet, options, index);
        final String actualColumnName = calculateColumnName(index + 1);
        final String indirectFormula = createFormulaForDropdown(selectionSheet, actualColumnName, options.size());
        createValidation(sheet, indirectFormula, columnIndex, startRowIndex, endRowIndex);
    }

    public static Sheet createSheet(Workbook workbook, String sheetName, boolean unlimitedWindowSize) {
        // 创建Sheet, windowSize控制在内存中一次操作sheet的量, 默认100, 超过后将刷到磁盘, 内存中的对象将被销毁
        final Sheet sheet = workbook.createSheet(sheetName);
        if (unlimitedWindowSize && sheet instanceof SXSSFSheet sxssfSheet) {
            sxssfSheet.setRandomAccessWindowSize(-1);
        }
        return sheet;
    }

    private static void createDropdownElement(Sheet sheet, Collection<Object> options, int columnIndex) {
        // 垂直方向生成元素, 从索引0行开始
        final AtomicInteger rowIndexAtomic = new AtomicInteger(0);
        options.forEach(option -> {
            final int rowIndex = rowIndexAtomic.getAndIncrement();
            final Row row = Optional.ofNullable(sheet.getRow(rowIndex)).orElseGet(() -> sheet.createRow(rowIndex));
            final Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellValue(String.valueOf(option));
        });
    }

    private static void createValidation(Sheet sheet, String validationFormula, int columnIndex, int startRowIndex, int endRowIndex) {
        // 创建约束范围
        final CellRangeAddressList addressList = new CellRangeAddressList(startRowIndex, endRowIndex, columnIndex, columnIndex);
        // 创建约束
        final DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        final DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(validationFormula);
        // 样式, 默认最严格方式, 禁止输入不在选项范围内的值
        final DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.createErrorBox("提示", "请选择下拉选项中的内容");

        sheet.addValidationData(dataValidation);
    }

    private static String createIndirectFormula(String columnName, int startRow) {
        return ExcelConstants.INDIRECT_FORMULA_FORMAT.formatted(columnName, startRow);
    }

    private static String createFormulaForNameManger(Sheet sheet, String columnName, int size) {
        return ExcelConstants.NAME_MANAGER_FORMULA_FORMAT.formatted(sheet.getSheetName(), columnName, "1", columnName, size);
    }

    private static String createFormulaForDropdown(Sheet sheet, String columnName, int size) {
        return ExcelConstants.DROPDOWN_FORMULA_FORMAT.formatted(sheet.getSheetName(), columnName, "1", columnName, size);
    }

    private static void createNameManager(Workbook workbook, Sheet sheet, Object originalNameName, String formula) {
        final String nameName = formatNameManager(originalNameName);
        //处理存在名称管理器复用的情况
        Name name = workbook.getName(nameName);
        if (name != null && Objects.equals(name.getSheetName(), sheet.getSheetName())) {
            return;
        }
        name = workbook.createName();
        name.setNameName(nameName);
        name.setSheetIndex(workbook.getSheetIndex(sheet));
        name.setRefersToFormula(formula);
    }

    public static void hideSheet(Workbook workbook, Sheet sheet) {
        // 隐藏Sheet
        final int sheetIndex = workbook.getSheetIndex(sheet);
        if (sheetIndex > -1) {
            workbook.setSheetHidden(sheetIndex, true);
        } else {
            log.error("Can't hide sheet={}, cause the sheet can't be found on the workbook!", sheet.getSheetName());
        }
    }

    private static String formatNameManager(Object name) {
        // 针对Excel不允许某些字符开头的情况, 使用下划线拼接可以实现, 在Indirect函数中再次拼接下划线即可完成数据关联
        return "_" + name;
    }

    private static String calculateColumnName(int columnIndex) {
        //获取到实际列名称, 例如 AAA 列
        final int minimumExponent = minimumExponent(columnIndex);
        final int base = 26, layers = (minimumExponent == 0 ? 1 : minimumExponent);
        final List<Character> sequence = Lists.newArrayList();
        int remain = columnIndex;
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

    private static int minimumExponent(int number) {
        final int base = 26;
        int exponent = 0;
        while (Math.pow(base, exponent) < number) {
            exponent++;
        }
        return exponent;
    }

    public static String getFilename(HttpServletRequest request, ResponseExcel responseExcel) {
        String name;
        try {
            name = ExcelContext.getFilename().orElseGet(
                    () -> Optional.ofNullable(request.getAttribute(ExcelConstants.FILENAME_ATTRIBUTE_KEY))
                            .map(Objects::toString).orElse(responseExcel.name())
            );
            if (StrUtil.isEmpty(name)) {
                name = UUID.randomUUID().toString();
            }
            return String.format("%s%s", URLEncoder.encode(name, StandardCharsets.UTF_8), responseExcel.suffix().getValue());
        } finally {
            ExcelContext.clearFilename();
        }
    }
}
