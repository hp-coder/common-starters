package com.hp.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class PageHelper {

    public static <T, E> Page<T> toPage(@NotNull PageRequestWrapper<E> requestWrapper) {
        return toPage(requestWrapper, "id", false);
    }

    public static <T, E> Page<T> toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull SFunction<T, ?> defaultOrderField) {
        return PageHelper.toPage(requestWrapper, TableHelper.columnName(defaultOrderField), false);
    }

    public static <T, E> Page<T> toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull SFunction<T, ?> defaultOrderField, boolean defaultAsc) {
        return PageHelper.toPage(requestWrapper, TableHelper.columnName(defaultOrderField), defaultAsc);
    }

    public static <T, E> Page<T> toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull String defaultSortField, boolean defaultAsc) {
        Page<T> page;
        final List<PageRequestWrapper.OrderColumn> sorts = requestWrapper.getSorts();
        if (CollUtil.isNotEmpty(sorts)) {
            final PageRequestWrapper.OrderColumn orderColumn = sorts.get(0);
            final boolean asc = orderColumn.getSortedOrder().equals(PageRequestWrapper.Order.asc);
            page = new Page<>(requestWrapper.getPage(), requestWrapper.getSize());
            final OrderItem item = new OrderItem();
            item.setAsc(asc);
            item.setColumn(orderColumn.getColumnName());
            page.setOrders(Lists.newArrayList(item));
        } else {
            page = new Page<>(requestWrapper.getPage(), requestWrapper.getSize());
            final OrderItem item = new OrderItem();
            item.setAsc(defaultAsc);
            item.setColumn(defaultSortField);
            page.setOrders(Lists.newArrayList(item));
        }
        return page;
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page) {
        return fromPage(page, Collections.emptyList());
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page, @NotNull List<E> records) {
        final Page<T> nonNullPage = Objects.requireNonNull(page);
        return PageResponse.of(Objects.requireNonNull(records), nonNullPage.getTotal(), Math.toIntExact(nonNullPage.getPages()), Math.toIntExact(nonNullPage.getSize()));
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page, @NotNull Function<T, E> mapping) {
        Preconditions.checkArgument(Objects.nonNull(page.getRecords()));
        final Page<T> nonNullPage = Objects.requireNonNull(page);
        final PageResponse<E> responsePage = PageResponse.of(null, nonNullPage.getTotal(), Math.toIntExact(nonNullPage.getPages()), Math.toIntExact(nonNullPage.getSize()));
        final List<E> converted = Objects.requireNonNull(nonNullPage.getRecords()).stream()
                .map(Objects.requireNonNull(mapping))
                .toList();
        responsePage.setList(converted);
        return responsePage;
    }

}
