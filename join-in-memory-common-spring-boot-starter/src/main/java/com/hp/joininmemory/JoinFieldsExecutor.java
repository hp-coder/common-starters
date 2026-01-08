package com.hp.joininmemory;

import java.util.Collection;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public interface JoinFieldsExecutor<DATA> {

    /**
     * 调用被处理对象的属性装载逻辑，这里用于指定串并行场景
     *
     * @param dataList 数据集合
     */
    void execute(Collection<DATA> dataList);

}
