package com.hp.jpa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class PageHelper {

    public static <E> PageRequest toPage(@NotNull PageRequestWrapper<E> requestWrapper) {
        return toPage(requestWrapper, "id", false);
    }

    public static <T, E> PageRequest toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull Func1<T, ?> defaultOrderField) {
        return PageHelper.toPage(requestWrapper, TableHelper.columnName(defaultOrderField), false);
    }

    public static <T, E> PageRequest toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull Func1<T, ?> defaultOrderField, boolean defaultAsc) {
        return PageHelper.toPage(requestWrapper, TableHelper.columnName(defaultOrderField), defaultAsc);
    }

    public static <E> PageRequest toPage(@NotNull PageRequestWrapper<E> requestWrapper, @NotNull String defaultSortField, boolean defaultAsc) {
        final List<PageRequestWrapper.OrderColumn> sorts = requestWrapper.getSorts();
        if (CollUtil.isNotEmpty(sorts)) {
            final List<Sort.Order> orders = sorts.stream()
                    .map(i -> i.getSortedOrder().isAsc() ? Sort.Order.asc(i.getColumnName()) : Sort.Order.desc(i.getColumnName()))
                    .collect(Collectors.toList());
            return PageRequest.of(requestWrapper.getPage() - 1, requestWrapper.getSize(), Sort.by(orders));
        } else {
            final Sort sort = Sort.by(defaultAsc ? Sort.Direction.ASC : Sort.Direction.DESC, defaultSortField);
            return PageRequest.of(requestWrapper.getPage() - 1, requestWrapper.getSize(), sort);
        }
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page) {
        return fromPage(page, Collections.emptyList());
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page, @NotNull List<E> records) {
        final Page<T> nonNullPage = Objects.requireNonNull(page);
        return PageResponse.of(Objects.requireNonNull(records), nonNullPage.getTotalElements(), nonNullPage.getNumber(), nonNullPage.getSize());
    }

    public static <T, E> PageResponse<E> fromPage(@NotNull Page<T> page, @NotNull Function<T, E> mapping) {
        final Page<T> nonNullPage = Objects.requireNonNull(page);
        final PageResponse<E> responsePage = PageResponse.of(null, nonNullPage.getTotalElements(), nonNullPage.getNumber(), nonNullPage.getSize());
        final List<E> converted = Objects.requireNonNull(nonNullPage.getContent()).stream()
                .map(Objects.requireNonNull(mapping))
                .toList();
        responsePage.setList(converted);
        return responsePage;
    }

}
