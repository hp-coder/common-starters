package com.hp.joininmemory.feature_nested_join;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026-2月-10
 */
@Repository
public class OrderRepository {

    public List<Order> selectByUserIds(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) return Collections.emptyList();

        return userIds.stream()
                .map(userId -> new Order(userId, userId))
                .toList();
    }

    public List<OrderItem> selectByOrderIds(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) return Collections.emptyList();

        return orderIds.stream()
                .map(orderId -> new OrderItem(orderId, orderId))
                .toList();
    }

    public List<Purchase> selectByOrderItemIds(Collection<Long> orderItemIds) {
        if (CollUtil.isEmpty(orderItemIds)) return Collections.emptyList();

        return orderItemIds.stream()
                .map(orderItemId -> new Purchase(orderItemId, orderItemId))
                .toList();
    }
}
