package com.hp.excel.listener;

import com.alibaba.excel.event.AnalysisEventListener;
import com.hp.excel.vo.ErrorMessage;

import java.util.List;

/**
 * @author HP
 * @date 2022/11/7
 */
public abstract class ExcelAnalysisEventListener<T> extends AnalysisEventListener<T> {

    public abstract List<T> getList();

    public abstract List<ErrorMessage> getErrors();
}
