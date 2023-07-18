package com.hp.joininmemory.example.domain.order.domainservice;

import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.example.domain.order.domainservice.model.OrderModel;
import com.hp.joininmemory.example.domain.order.mapper.OrderMapper;
import com.hp.joininmemory.example.domain.order.response.OrderVO;
import com.hp.joininmemory.example.domain.order.service.IOrderService;
import com.hp.joininmemory.example.domain.orderitem.mapper.OrderItemMapper;
import com.hp.joininmemory.example.domain.orderitem.response.OrderItemVO;
import com.hp.joininmemory.example.domain.orderitem.service.IOrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author hp 2023/5/4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements IOrderDomainService {

    private final IOrderService orderService;
    private final IOrderItemService orderItemService;
    private final JoinService joinService;


    @Override
    public Page<OrderModel> findByPageUsingTraditionalMappingJoin() {
        // find orders
        final Page<OrderVO> orders = orderService.findByPage();
        // find order items by orderIds
        final List<Long> orderIds = orders.getContent().stream().map(OrderVO::getId).collect(Collectors.toList());
        List<OrderItemVO> orderItems = orderItemService.findAllByOrderIds(orderIds);
        // order id keyed mapping
        final Map<Long, List<OrderItemVO>> orderItemsMapping = orderItems.stream().collect(Collectors.groupingBy(OrderItemVO::getOrderId));
        // load the items from the mapping
        final List<OrderModel> models = orders.getContent()
                .stream().map(vo -> {
                    OrderModel model = OrderMapper.INSTANCE.voToModel(vo);
                    Optional.ofNullable(orderItemsMapping.getOrDefault(vo.getId(), null))
                            .ifPresent(item-> model.setOrderItems(item.stream().map(OrderItemMapper.INSTANCE::voToResponse).collect(Collectors.toList())));
                    return model;
                })
                .collect(Collectors.toList());
        // return the results
        return new PageImpl<>(models);
    }

    @Override
    public Page<OrderModel> findByPageUsingAnnotationDrivenJoin() {
        // find orders
        final Page<OrderVO> orders = orderService.findByPage();
        // convert to models
        final List<OrderModel> models = orders.getContent()
                .stream()
                .map(OrderMapper.INSTANCE::voToModel)
                .collect(Collectors.toList());
        // join in memory
        joinService.joinInMemory(OrderModel.class, models);
        // return the results
        return new PageImpl<>(models);
    }
}
