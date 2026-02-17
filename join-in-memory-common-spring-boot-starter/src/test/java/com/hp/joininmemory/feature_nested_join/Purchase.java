package com.hp.joininmemory.feature_nested_join;

import com.hp.joininmemory.annotation.AfterJoin;
import lombok.Data;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026-2月-11
 */
@Data
public class Purchase {

    private Long orderItemId;

    private Long productId;

    public Purchase(Long orderItemId, Long productId) {
        this.orderItemId = orderItemId;
        this.productId = productId;
    }

    @AfterJoin
    public void afterJoin() {
        System.out.println("Purchase After Join: " + this);
    }
}
