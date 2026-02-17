package com.hp.joininmemory.feature_join_at_return_dynamic_join_fields;

import com.hp.joininmemory.JoinInMemoryApplication;
import jakarta.annotation.Resource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@SpringBootTest(classes = JoinInMemoryApplication.class)
public class JoinAtReturnDynamicJoinFieldTest {

    @Resource
    private OrderService orderService;


    @Test
    public void givenOrderId_whenGetOrderById_thenReturnOrder() {
        // given
        final OrderService service = orderService;

        // when
        Order order = service.getOrderByIdExcludeCreator(1L);

        final List<Order> orders = service.getOrdersByIdsIncludeUpdater(List.of(2L, 3L));

        final OrderPage orderPage = service.getOrderPageByIdsIncludeCreator(List.of(4L, 5L));

        // then
        Assertions.assertThat(order.getCreator()).isNull();
        Assertions.assertThat(order.getUpdater()).isNotEmpty();

        Assertions.assertThat(orders.get(0).getCreator()).isNull();
        Assertions.assertThat(orders.get(0).getUpdater()).isNotEmpty();

        Assertions.assertThat(orders.get(orders.size()-1).getCreator()).isNull();
        Assertions.assertThat(orders.get(orders.size()-1).getUpdater()).isNotEmpty();

        Assertions.assertThat(orderPage.getRows().get(0).getCreator()).isNotEmpty();
        Assertions.assertThat(orderPage.getRows().get(0).getUpdater()).isNull();

        Assertions.assertThat(orderPage.getRows().get(orders.size()-1).getCreator()).isNotEmpty();
        Assertions.assertThat(orderPage.getRows().get(orders.size()-1).getUpdater()).isNull();

    }
}
