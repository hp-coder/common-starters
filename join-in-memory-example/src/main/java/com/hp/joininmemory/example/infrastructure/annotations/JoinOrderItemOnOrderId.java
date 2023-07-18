package com.hp.joininmemory.example.infrastructure.annotations;

import com.hp.joininmemory.annotation.JoinInMemory;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hp 2023/4/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JoinInMemory(
        keyFromSourceData = "",
        keyFromJoinData = "#{orderId}",
        loader = "#{@orderItemServiceImpl.findAllByOrderIds(#root)}",
        joinDataConverter = "#{T(com.hp.joininmemory.example.domain.orderitem.mapper.OrderItemMapper).INSTANCE.voToResponse(#root)}",
        runLevel = 1
)
public @interface JoinOrderItemOnOrderId {

    @AliasFor(annotation = JoinInMemory.class)
    String keyFromSourceData();
}
