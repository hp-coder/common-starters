package com.hp.excel.handler;


import com.hp.excel.annotation.ResponseExcel;

import javax.servlet.http.HttpServletResponse;

/**
 * 要么单页 要么多页*
 * @author HP
 * @date 2022/11/7
 */
public interface ExcelSheetWriteHandler {
    boolean support(Object obj);

    void check(ResponseExcel responseExcel);

    void export(Object o, HttpServletResponse response, ResponseExcel responseExcel) throws Exception;

    void write(Object o, HttpServletResponse response, ResponseExcel responseExcel);
}
