package com.hp.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.Request;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp 2023/3/27
 */
public final class PageOrderHelper {

    public static <T extends Request> List<OrderItem> orders(PageRequestWrapper<T> wrapper) {
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
