package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.InvalidOrderQuantityException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.exception.ProductNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

  private static final String ORDER_ITEM_NOT_FOUND = "Order item not found! ID: ";
  private static final String ITEM_NOT_FOUND = "Item not found! ID: ";

  private final OrderItemRepository orderItemRepository;
  private final OrderItemMapper orderItemMapper;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;

  @Override
  public OrderItemResponse createOrderItem(CreateOrderItemRequest orderItemCreateRequest) {
    Order order = orderRepository.findById(orderItemCreateRequest.getOrderId())
        .orElseThrow(() -> new OrderNotFoundException(
            ORDER_ITEM_NOT_FOUND + orderItemCreateRequest.getOrderId()));
    Item item = itemRepository.findById(orderItemCreateRequest.getItemId())
        .orElseThrow(() -> new ProductNotFoundException(
            ITEM_NOT_FOUND + orderItemCreateRequest.getItemId()));

    OrderItem orderItem = new OrderItem();
    orderItem.setOrder(order);
    orderItem.setItem(item);
    orderItem.setQuantity(orderItemCreateRequest.getQuantity());
    return orderItemMapper.toResponse(orderItemRepository.save(orderItem));
  }

  @Override
  public OrderItemResponse getOrderItemById(Long orderItemId) {
    OrderItem orderItem = orderItemRepository.findById(orderItemId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_ITEM_NOT_FOUND + orderItemId));
    return orderItemMapper.toResponse(orderItem);
  }

  @Override
  public List<OrderItemResponse> getAllOrderItems() {
    List<OrderItem> orderItems = orderItemRepository.findAll();
    return orderItems.stream().map(orderItemMapper::toResponse).toList();
  }

  @Override
  @Transactional
  public OrderItemResponse updateOrderItem(Long orderItemId,
      OrderItemRequest orderItemUpdateRequest) {
    OrderItem orderItem = orderItemRepository.findById(orderItemId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_ITEM_NOT_FOUND + orderItemId));
    if (orderItemUpdateRequest.getQuantity() <= 0) {
      throw new InvalidOrderQuantityException("Quantity must be greater than 0!");
    }
    if (orderItemUpdateRequest.getItemId() != null) {
      Item item = itemRepository.findById(orderItemUpdateRequest.getItemId())
          .orElseThrow(() -> new ProductNotFoundException(
              ITEM_NOT_FOUND + orderItemUpdateRequest.getItemId()));
      orderItem.setItem(item);
    }
    orderItem.setQuantity(orderItemUpdateRequest.getQuantity());
    return orderItemMapper.toResponse(orderItemRepository.save(orderItem));
  }

  @Override
  @Transactional
  public void deleteOrderItem(Long orderItemId) {
    OrderItem orderItem = orderItemRepository.findById(orderItemId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_ITEM_NOT_FOUND + orderItemId));
    orderItemRepository.delete(orderItem);
  }
}
