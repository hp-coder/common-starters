package com.luban.excel.handler;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.luban.excel.annotation.ResponseExcel;
import org.springframework.beans.factory.ObjectProvider;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author HP
 * @date 2022/11/7
 */
public class SingleSheetWriteHandler extends AbstractExcelSheetWriteHandler {

    public SingleSheetWriteHandler(ObjectProvider<List<Converter<?>>> converterProvider) {
        super(converterProvider);
    }

    @Override
    public boolean support(Object obj) {
        if (!(obj instanceof List)) {
            throw new IllegalArgumentException("@ResponseExcel annotation works only when the return type is a List");
        }
        final List<?> list = (List<?>) obj;
        return !list.isEmpty() && !(list.get(0) instanceof List);
    }

    @Override
    public void write(Object o, HttpServletResponse response, ResponseExcel responseExcel) {
        try {
            final List<?> list = (List) o;
            final Class<?> dataClass = list.get(0).getClass();
            final ExcelWriter excelWriter = this.getExcelWriter(response, responseExcel, dataClass);
            final WriteSheet sheet = this.sheet(responseExcel.sheets()[0], dataClass, responseExcel.template(), responseExcel.headGenerator());
            if (responseExcel.fill()) {
                excelWriter.fill(list, sheet);
            } else {
                excelWriter.write(list, sheet);
            }
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
