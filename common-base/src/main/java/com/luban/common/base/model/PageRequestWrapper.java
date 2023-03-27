package com.luban.common.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;


/**
 * @author hp
 */
@Data
public class PageRequestWrapper<T> {

    private T bean;
    private Integer pageSize;
    private Integer page;
    private List<OrderColumn> sorts;

    @AllArgsConstructor
    @Getter
    public static class OrderColumn {
        private final String columnName;
        private Order sortedOrder;
    }

    public static OrderColumn desc(String columnName) {
        return new OrderColumn(columnName, Order.desc);
    }

    public static OrderColumn asc(String columnName) {
        return new OrderColumn(columnName, Order.asc);
    }

    public enum Order {
        /**
         * 排序
         */
        asc,
        desc,
    }
}
