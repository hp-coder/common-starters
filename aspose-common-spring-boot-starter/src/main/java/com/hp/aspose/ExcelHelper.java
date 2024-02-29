package com.hp.aspose;

import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.hp.aspose.exception.AsposeException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hp 2023/4/20
 */

public final class ExcelHelper {
    private ExcelHelper(){}
    public static void xlsToXlsx(InputStream inputStream, OutputStream outputStream) {
        try {
            Workbook workbook = new Workbook(inputStream);
            final WorksheetCollection worksheets = workbook.getWorksheets();
            worksheets.setActiveSheetIndex(0);
            workbook.save(outputStream, 6);
        } catch (Exception e) {
            throw new AsposeException("XlsToXlsx Exception", e);
        }
    }
}
