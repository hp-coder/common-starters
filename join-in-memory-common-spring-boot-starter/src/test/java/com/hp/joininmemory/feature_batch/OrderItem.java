package com.hp.joininmemory.feature_batch;

import lombok.Data;

@Data
public class OrderItem {

    private Long orderId;

    private Long orderItemId;

    public OrderItem(Long orderId, Long orderItemId) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
    }
}
