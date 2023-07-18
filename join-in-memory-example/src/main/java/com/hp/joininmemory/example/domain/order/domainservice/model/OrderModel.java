package com.hp.joininmemory.example.domain.order.domainservice.model;

import com.hp.common.base.annotations.FieldDesc;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.annotation.JoinInMemoryExecutorType;
import com.hp.joininmemory.example.domain.orderitem.response.OrderItemResponse;
import com.hp.joininmemory.example.infrastructure.annotations.JoinOrderItemOnOrderId;
import lombok.Data;

import java.util.List;

/**
 * @author hp 2023/5/4
 */
@Data
@JoinInMemoryConfig(executorType = JoinInMemoryExecutorType.PARALLEL)
public class OrderModel {

    @FieldDesc("订单id")
    private Long orderId;

    @FieldDesc("订单号")
    private String orderNo;

    @FieldDesc("状态")
    private Integer status;

    @JoinOrderItemOnOrderId(keyFromSourceData = "#{orderId}")
    @FieldDesc("订单项")
    private List<OrderItemResponse> orderItems;
}
