package com.luban.common.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;


/**
 * @author hp
 */
@Data
public class PageRequestWrapper<T> {

    private Integer page;
    private Integer pageSize;
    private T bean;
    private List<OrderColumn> sorts;

    public PageRequestWrapper(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        validate();
    }

    public PageRequestWrapper(Integer page, Integer pageSize, T bean) {
        this.page = page;
        this.pageSize = pageSize;
        this.bean = bean;
        validate();
    }

    public PageRequestWrapper(Integer page, Integer pageSize, T bean, List<OrderColumn> sorts) {
        this.page = page;
        this.pageSize = pageSize;
        this.bean = bean;
        this.sorts = sorts;
        validate();
    }

    private void validate() {
        Assert.isTrue(page != null && page > 0, "页码异常");
        Assert.isTrue(pageSize != null && pageSize > 0, "每页记录数异常");
    }

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
