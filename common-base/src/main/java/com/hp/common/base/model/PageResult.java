package com.hp.common.base.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author hp
 */
@Getter
@Setter
public class PageResult<T> {
    private Long total;
    private Integer pageSize;
    private Integer pageNumber;
    private List<T> list;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total, Integer pageSize, Integer pageNumber) {
        this.list = list;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public static <T> PageResult of(List<T> list, Long total, Integer pageSize, Integer pageNumber) {
        return new PageResult(list, total, pageSize, pageNumber);
    }
}
