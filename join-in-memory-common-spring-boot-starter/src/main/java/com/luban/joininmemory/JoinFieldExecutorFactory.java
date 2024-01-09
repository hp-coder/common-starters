package com.luban.joininmemory;

import com.luban.joininmemory.context.JoinContext;

import java.util.List;

/**
 * @author programmer
 */
public interface JoinFieldExecutorFactory{

    /**
     * 构建对应属性的数据装载执行器
     *
     * @param clazz  数据对象中每个定义了 {@link  com.luban.joininmemory.annotation.JoinInMemory} 的属性对应的类型
     * @param <DATA> 属性
     * @return 一个数据装载处理器的集合
     */
    <DATA> List<JoinFieldExecutor<DATA>> createForType(JoinContext<DATA> joinContext);
}
