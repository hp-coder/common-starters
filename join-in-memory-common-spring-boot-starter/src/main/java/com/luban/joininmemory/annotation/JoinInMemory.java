package com.luban.joininmemory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author hp
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemory {
    /**
     * 从 sourceData 中提取 key
     * 如：查询订单列表时，关联用户数据的key为userId
     */
    String keyFromSourceData();

    /**
     * 从 joinData 中提取 key
     * 如：查询到用户所有订单后，通过userId，查询到关联的用户信息，用户信息中与订单关联的信息为其id
     * <p>
     * 用于构造Map映射关系的key值
     */
    String keyFromJoinData();

    /**
     * 批量数据抓取
     * 如：查询订单列表，订单通过userId关联用户信息，使用什么方法通过userId获取到用户信息
     */
    String loader();

    /**
     * 结果转换器
     * 如：查询到关联的用户信息时通过ORM映射返回的用户对象，在业务中常需要转换为VO及其他对象，如何转换由这里定义
     */
    String joinDataConverter() default "";

    /**
     * 运行级别，同一级别的 join 可 并行执行
     */
    int runLevel() default 10;
}
