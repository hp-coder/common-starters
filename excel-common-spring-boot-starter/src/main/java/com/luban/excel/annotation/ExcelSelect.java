package com.luban.excel.annotation;

import com.luban.excel.handler.DefaultColumnDynamicSelectDataHandler;
import com.luban.excel.handler.ColumnDynamicSelectDataHandler;

import java.lang.annotation.*;

/**
 * 添加下拉列表注解
 * <p>
 * 支持静态，动态字符串数据源*
 * 以及二级级联下拉列表数据源*
 *
 * @author HP
 * @date 2022/11/7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ExcelSelect {

    /**
     * 静态数据 只支持 字符串数组，如果要使用级联，请使用处理器获取数据*
     *
     * @return
     */
    String[] staticData() default {};

    /**
     * 关联父列名称*
     *
     * @return
     */
    String parentColumn() default "";

    /**
     * 动态数据*
     *
     * @return
     */
    Class<? extends ColumnDynamicSelectDataHandler> handler() default DefaultColumnDynamicSelectDataHandler.class;

    /**
     * 主要是提供一些简单参数*
     *
     * @return
     */
    String parameter() default "";

    /**
     * 设置下拉框的起始行，默认为第二行
     */
    int firstRow() default 1;

    /**
     * 设置下拉框的结束行，默认为最后一行
     */
    int lastRow() default 0x10000;
}
