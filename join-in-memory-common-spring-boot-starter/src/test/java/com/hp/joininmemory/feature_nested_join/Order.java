package com.hp.joininmemory.feature_nested_join;

import com.hp.joininmemory.annotation.AfterJoin;
import com.hp.joininmemory.annotation.NestedJoin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Data
@NoArgsConstructor
public class Order {

    private Long id;

    private Long userId;

    @NestedJoin
    @JoinOrderItemOnOrderId(value = "id")
    private List<OrderItem> orderItems;

    public Order(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    @AfterJoin
    public void orderAfterJoin() {
        System.out.println("Order After Join!!!");
    }
}
