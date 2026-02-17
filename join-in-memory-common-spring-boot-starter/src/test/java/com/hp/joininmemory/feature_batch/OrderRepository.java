package com.hp.joininmemory.feature_batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OrderRepository {

    private static final AtomicInteger batchCounter = new AtomicInteger(1);

    public List<OrderItem> findByOrderId(Collection<Long> orderIds) {
        log.error("这是第{}批查询: 这个batch大小={}",batchCounter.getAndIncrement(), orderIds.size());

        return orderIds.stream()
                .map(i -> new OrderItem(i, i))
                .collect(Collectors.toList());
    }
}
