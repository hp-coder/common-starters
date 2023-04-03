package com.luban.joininmemory;

import java.util.List;

/**
 * @author programmer

 */
public interface JoinFieldExecutorFactory {

    /**
     * 构建对应属性的数据装载执行器
     * @param clazz 数据对象中每个定义了 {@link  com.luban.joininmemory.annotation.JoinInMemory} 的属性对应的类型
     * @param <TYPE> 属性
     * @return 一个数据装载处理器的集合
     */
    <TYPE> List<JoinFieldExecutor<TYPE>> createForType(Class<TYPE> clazz);
}
