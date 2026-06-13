package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  OrderResponse createOrder(OrderRequest orderCreateRequest);

  OrderResponse getOrderById(Long orderId);

  Page<OrderResponse> getAllOrders(Instant from, Instant to, List<OrderStatus> statuses, Pageable page);

  List<OrderResponse> getOrdersByUserId(UUID userId);

  OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);

  void deleteOrder(Long orderId);
}
