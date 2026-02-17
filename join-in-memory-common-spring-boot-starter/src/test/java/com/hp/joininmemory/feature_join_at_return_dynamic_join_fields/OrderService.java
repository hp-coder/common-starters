package com.hp.joininmemory.feature_join_at_return_dynamic_join_fields;


import com.hp.joininmemory.annotation.JoinAtReturn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Slf4j
@Service
public class OrderService {

    @JoinAtReturn(excluded = {"creator"})
    public Order getOrderByIdExcludeCreator(Long id) {
        log.info("orderId={}", id);
        // 模拟从数据库中获取订单信息
        return new Order(id, id + 1, id + 2);
    }

    @JoinAtReturn(included = {"updater"})
    public List<Order> getOrdersByIdsIncludeUpdater(List<Long> ids) {
        log.info("orderIds={}", ids);
        return ids.stream()
                .map(id -> new Order(id, id + 1, id + 2))
                .toList();
    }

    @JoinAtReturn(value = "#this.rows", included = {"creator"})
    public OrderPage getOrderPageByIdsIncludeCreator(List<Long> ids) {
        log.info("orderPageIds={}", ids);
        final List<Order> orders = ids.stream()
                .map(id -> new Order(id, id + 1, id + 2))
                .toList();
        return new OrderPage(orders);
    }
}
