package com.hp.joininmemory.example.domain.order.domainservice;

import com.hp.joininmemory.example.domain.order.domainservice.model.OrderModel;
import org.springframework.data.domain.Page;

/**
 * @author hp 2023/5/4
 */
public interface IOrderDomainService {
    Page<OrderModel> findByPageUsingTraditionalMappingJoin();

    Page<OrderModel> findByPageUsingAnnotationDrivenJoin();

}
