package com.luban.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.luban.common.base.model.PageRequestWrapper;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp 2023/3/27
 */
public final class PageOrderHelper {

    public static <T> List<OrderItem> orders(PageRequestWrapper<T> wrapper) {
        final List<PageRequestWrapper.OrderColumn> sorts = wrapper.getSorts();
        if (CollectionUtils.isEmpty(sorts)) {
            return Collections.emptyList();
        }
        return sorts.stream().map(i -> {
            if (Objects.equals(i.getSortedOrder(), PageRequestWrapper.Order.desc)) {
                return OrderItem.desc(i.getColumnName());
            } else {
                return OrderItem.asc(i.getColumnName());
            }
        }).collect(Collectors.toList());
    }
}
