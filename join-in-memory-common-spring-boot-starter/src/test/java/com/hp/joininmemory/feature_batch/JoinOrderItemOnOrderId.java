package com.hp.joininmemory.feature_batch;


import com.hp.joininmemory.annotation.JoinInMemory;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JoinInMemory(
        keyFromJoinData = "orderId",
        loader = "@orderRepository.findByOrderId(#root)"
)
public @interface JoinOrderItemOnOrderId {

    @AliasFor(annotation = JoinInMemory.class, value = "keyFromSourceData")
    String value();
}
