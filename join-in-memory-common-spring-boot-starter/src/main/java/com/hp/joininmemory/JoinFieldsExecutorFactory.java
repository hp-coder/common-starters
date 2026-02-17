package com.hp.joininmemory;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public interface JoinFieldsExecutorFactory {
    /**
     * 为类创建 Join 执行器
     *
     * @param clazz  需要装载数据的类型；如：订单列表的 OrderVO.class
     * @param <DATA> 如：OrderVO
     * @return 一个包含了该对象所有支持注解的所有属性的对应的处理器集合
     */
    <DATA> JoinFieldsExecutor<DATA> createFor(Class<DATA> clazz);
}
