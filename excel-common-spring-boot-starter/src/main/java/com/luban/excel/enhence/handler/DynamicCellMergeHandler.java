package com.luban.excel.enhence.handler;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO cell级别的合并*
 * @author hp
 */
public class DynamicCellMergeHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        final Cell cell = context.getCell();

    }
}
