package com.hp.joininmemory.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.joininmemory.example.domain.order.domainservice.IOrderDomainService;
import com.hp.joininmemory.example.domain.order.domainservice.model.OrderModel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author hp 2023/5/4
 */
@SpringBootTest
public class OrderTests {

    @Autowired
    private IOrderDomainService orderDomainService;

    @Test
    @SneakyThrows
    public void test_find_order_pages_using_traditional_mapping_join() {
        final Page<OrderModel> page = orderDomainService.findByPageUsingTraditionalMappingJoin();
        final ObjectMapper objectMapper = new ObjectMapper();
        page.getContent().forEach(order -> assertNotNull(order.getOrderItems()));
        System.out.println(objectMapper.writeValueAsString(page));
    }

    @Test
    @SneakyThrows
    public void test_find_order_pages_using_annotation_driven_join() {
        final Page<OrderModel> page = orderDomainService.findByPageUsingAnnotationDrivenJoin();
        final ObjectMapper objectMapper = new ObjectMapper();
        page.getContent().forEach(order -> assertNotNull(order.getOrderItems()));
        System.out.println(objectMapper.writeValueAsString(page));
    }
}
