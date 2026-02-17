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
public class OrderItem {

    private Long id;

    private Long orderId;

    @NestedJoin
    @JoinPurchaseOnOrderItemId("id")
    private List<Purchase> purchases;

    public OrderItem(Long orderId, Long id) {
        this.orderId = orderId;
        this.id = id;
    }

    @AfterJoin
    public void orderItemAfterJoin() {
        System.out.println("OrderItem After Join!!!");
        System.out.println("this.purchases = " + this.purchases);
    }
}
