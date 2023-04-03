package com.luban.joininmemory;

import java.util.List;

/**
 *
 * @author programmer
 */
public interface JoinFieldsExecutor<DATA> {

    /**
     * 调用被处理对象的属性装载逻辑，这里用于指定串并行场景
     * @param dataList 数据集合
     */
    void execute(List<DATA> dataList);
}
