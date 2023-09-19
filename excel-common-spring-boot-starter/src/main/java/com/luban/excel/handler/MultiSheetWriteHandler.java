package com.luban.excel.handler;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.luban.excel.annotation.ResponseExcel;
import com.luban.excel.annotation.Sheet;
import org.springframework.beans.factory.ObjectProvider;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author hp
 * @date 2022/11/7
 */
public class MultiSheetWriteHandler extends AbstractExcelSheetWriteHandler {

    public MultiSheetWriteHandler(ObjectProvider<List<Converter<?>>> converterProvider) {
        super(converterProvider);
    }

    @Override
    public boolean support(Object obj) {
        if (!(obj instanceof List)) {
            throw new IllegalArgumentException("@ResponseExcel annotation works only when the return type is a List");
        }
        final List<?> list = (List<?>) obj;
        return !list.isEmpty() && (list.get(0) instanceof List);
    }

    @Override
    public void write(Object o, HttpServletResponse response, ResponseExcel responseExcel) {
        try {
            List<?> list = (List) o;
            final List<?> itemList = (List) list.get(0);
            ExcelWriter excelWriter = this.getExcelWriter(response, responseExcel, itemList.get(0).getClass());
            Sheet[] sheets = responseExcel.sheets();

            for (int i = 0; i < sheets.length; ++i) {
                List<?> eleList = (List) list.get(i);
                Class<?> dataClass = eleList.get(0).getClass();
                WriteSheet sheet = this.sheet(sheets[i], dataClass, responseExcel.template(), responseExcel.headGenerator());
                if (responseExcel.fill()) {
                    excelWriter.fill(eleList, sheet);
                } else {
                    excelWriter.write(eleList, sheet);
                }
            }
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
