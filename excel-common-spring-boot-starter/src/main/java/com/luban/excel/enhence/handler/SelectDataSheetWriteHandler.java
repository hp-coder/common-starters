package com.luban.excel.enhence.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.luban.excel.dto.ExcelSelectDataColumn;
import com.luban.excel.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;

/**
 * 请照抄整个类*
 * @author HP
 * @date 2022/11/7
 */
@RequiredArgsConstructor
public class SelectDataSheetWriteHandler implements SheetWriteHandler {

    private final Map<Integer, ExcelSelectDataColumn> selectedMap;

    /**
     * Called before create the sheet
     */
    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    /**
     * Called after the sheet is created
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 这里可以对cell进行任何操作
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        selectedMap.forEach((k, v) -> {
            // 设置下拉列表的行： 首行，末行，首列，末列
            if (StrUtil.isNotEmpty(v.getParentColumn())) {
                final Map<String, List<String>> data = (Map<String, List<String>>) v.getSource();
                //直接粘贴该工具类方法到你的项目中
                ExcelUtil.addCascadeValidationToSheet(writeWorkbookHolder, writeSheetHolder, data, v.getParentColumnIndex(), k, v.getFirstRow(), v.getLastRow());
            } else {
                //直接粘贴该工具类方法到你的项目中
                ExcelUtil.addSelectValidationToSheet(sheet, helper, k, v);
            }
        });
    }

}
