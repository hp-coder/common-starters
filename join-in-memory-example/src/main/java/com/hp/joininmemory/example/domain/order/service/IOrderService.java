// --- Auto Generated By CodeGen Module ---
package com.hp.joininmemory.example.domain.order.service;

import com.hp.joininmemory.example.domain.order.request.OrderDTO;
import com.hp.joininmemory.example.domain.order.response.OrderVO;
import org.springframework.data.domain.Page;

public interface IOrderService {
  Long createOrder(OrderDTO creator);

  void updateOrder(OrderDTO updater);

  void validOrder(Long id);

  void invalidOrder(Long id);

  OrderVO findById(Long id);

  OrderVO findByOrderNo(String orderNo);

  Page<OrderVO> findByPage();
}
