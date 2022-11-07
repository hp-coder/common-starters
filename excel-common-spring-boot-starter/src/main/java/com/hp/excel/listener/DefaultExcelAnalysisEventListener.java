package com.hp.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.hp.excel.vo.ErrorMessage;

import java.util.List;

/**
 * @author HP
 * @date 2022/11/7
 */
public class DefaultExcelAnalysisEventListener extends ExcelAnalysisEventListener<Object> {

    @Override
    public List<Object> getList() {
        return null;
    }

    @Override
    public List<ErrorMessage> getErrors() {
        return null;
    }

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
