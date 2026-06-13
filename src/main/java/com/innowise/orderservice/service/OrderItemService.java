package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import java.util.List;

public interface OrderItemService {

  OrderItemResponse createOrderItem(CreateOrderItemRequest orderItemCreateRequest);

  OrderItemResponse getOrderItemById(Long orderItemId);

  List<OrderItemResponse> getAllOrderItems();

  OrderItemResponse updateOrderItem(Long orderItemId, OrderItemRequest orderItemUpdateRequest);

  void deleteOrderItem(Long orderItemId);
}
