package com.hp.excel.enhence.handler;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO cell级别的合并*
 * @author HP 2022/11/9
 */
public class DynamicCellMergeHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        final Cell cell = context.getCell();

    }
}
