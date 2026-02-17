package com.hp.joininmemory.feature_batch;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import lombok.Data;

import java.util.List;

@Data
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.PARALLEL,
        fieldProcessPolicy = JoinFieldProcessPolicy.SEPARATED,
        joinBatchSize = 20
)
public class ParallelAndGroupOrder {

    private Long orderId;

    @JoinOrderItemOnOrderId("orderId")
    private List<OrderItem> orderItems;

    public ParallelAndGroupOrder(Long orderId) {
        this.orderId = orderId;
    }
}
