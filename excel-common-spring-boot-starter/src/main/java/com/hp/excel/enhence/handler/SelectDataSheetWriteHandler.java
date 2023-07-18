package com.hp.excel.enhence.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.hp.excel.dto.ExcelSelectDataColumn;
import com.hp.excel.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 请照抄整个类*
 *
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
        //尽量少的创建sheet, 也可以只用一个额外的sheet放这些下拉数据
        AtomicReference<Sheet> tmpSheet = new AtomicReference<>(null);
        AtomicReference<Sheet> tmpCascadeSheet = new AtomicReference<>(null);
        AtomicInteger tmpSheetStartCol = new AtomicInteger(0);
        AtomicInteger tmpCascadeSheetStartCol = new AtomicInteger(0);

        selectedMap.forEach((colIndex, model) -> {
            if (StrUtil.isNotEmpty(model.getParentColumn())) {
                //直接粘贴该工具类方法到你的项目中
                tmpCascadeSheet.set(
                        ExcelUtil.addCascadeValidationToSheet(
                                writeWorkbookHolder,
                                writeSheetHolder,
                                tmpCascadeSheet.get(),
                                (Map<String, List<String>>) model.getSource(),
                                tmpCascadeSheetStartCol,
                                model.getParentColumnIndex(),
                                colIndex,
                                model.getFirstRow(),
                                model.getLastRow()
                        )
                );
            } else {
                //直接粘贴该工具类方法到你的项目中
                tmpSheet.set(
                        ExcelUtil.addSelectValidationToSheet(
                                writeWorkbookHolder,
                                writeSheetHolder,
                                tmpSheet.get(),
                                (List<String>) model.getSource(),
                                tmpSheetStartCol,
                                colIndex,
                                model.getFirstRow(),
                                model.getLastRow()
                        )
                );
            }
        });
    }

}
