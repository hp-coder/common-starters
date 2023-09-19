package com.luban.excel.listener;

import com.alibaba.excel.event.AnalysisEventListener;
import com.luban.excel.vo.ErrorMessage;

import java.util.List;

/**
 * 默认情况下*
 * 逻辑时首先将行数据通过ModelBuildEventListener包装成实体对象然后再通过自定义处理器处理*
 * @author hp
 * @date 2022/11/7
 */
public abstract class ExcelAnalysisEventListener<T,D> extends AnalysisEventListener<T> {

    public abstract D getData();

    public abstract List<ErrorMessage> getErrors();
}
