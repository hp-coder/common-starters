package com.hp.joininmemory.feature_nested_join;

import com.hp.common.base.utils.SpELHelper;
import com.hp.joininmemory.JoinInMemoryApplication;
import com.hp.joininmemory.JoinService;
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
public class NestedJoinTest {


    @Resource
    JoinService joinService;
    @Resource
    SpELHelper spELHelper;

    @Test
    public void givenSpELHelper_WhenJoinWithNestedJoin_thenSuccess() {

        // given
          User user = new User(1L, "User_1");

        //  when
        joinService.joinInMemory(user);
        user = new User(1L, "User_1");
        joinService.joinInMemory(user);

        // then
//        final List<Order> orders = user.getOrders();
//        Assertions.assertThat(orders).isNotEmpty().size().isEqualTo(1);
//        Assertions.assertThat(orders).element(0).isNotNull().extracting(Order::getOrderItems).isNotNull();
        final Order order1 = user.getOrder();
        final List<OrderItem> orderItems = order1.getOrderItems();
        final OrderItem orderItem1 = orderItems.getFirst();
        Assertions.assertThat(orderItem1).isNotNull().extracting(OrderItem::getPurchases).isNotNull();

        final List<Purchase> purchases = orderItem1.getPurchases();
        Assertions.assertThat(purchases).isNotEmpty().size().isEqualTo(1);
    }

    @Test
    public void givenSpELToExtractOrderFromUser_whenCalling_thenSuccess(){

        // given
        final User user = new User(1L, "User_1");
        user.setOrder(new Order(1L, 1L));

        String expression = "order";

        // when
        final Object order = spELHelper.newGetterInstance(expression).apply(user);

        // then
        Assertions.assertThat(order).isNotNull().isInstanceOf(Order.class);
        final Order actualOrder = (Order) order;
        System.out.println("actualOrder = " + actualOrder);
    }
}
