package com.innowise.orderservice.unittest;

import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.OrderItemRepository;
import com.innowise.orderservice.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.innowise.orderservice.exception.InvalidOrderQuantityException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.repository.OrderRepository;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.exception.ProductNotFoundException;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

  @Mock
  private OrderItemRepository orderItemRepository;

  @Mock
  private OrderItemMapper orderItemMapper;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private OrderItemServiceImpl orderItemService;

  private Order order;
  private Item item;
  private OrderItem orderItem;
  private OrderItemResponse response;

  @BeforeEach
  void setUp() {
    order = Order.builder()
        .id(1L)
        .build();

    item = Item.builder()
        .id(1L)
        .name("iPhone")
        .price(BigDecimal.valueOf(100))
        .build();

    orderItem = OrderItem.builder()
        .id(1L)
        .order(order)
        .item(item)
        .quantity(2)
        .build();

    response = new OrderItemResponse();
    response.setId(1L);
  }

  @Test
  void should_success_createOrderItem() {
    CreateOrderItemRequest request = new CreateOrderItemRequest();

    request.setOrderId(1L);
    request.setItemId(1L);
    request.setQuantity(2);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
    when(orderItemMapper.toResponse(orderItem)).thenReturn(response);

    OrderItemResponse result = orderItemService.createOrderItem(request);

    assertNotNull(result);
    verify(orderItemRepository).save(any(OrderItem.class));
  }

  @Test
  void should_throwException_createOrderItem_whenOrderNotFound() {
    CreateOrderItemRequest request = new CreateOrderItemRequest();

    request.setOrderId(1L);
    request.setItemId(1L);

    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderItemService.createOrderItem(request)
    );
  }

  @Test
  void should_throwException_createOrderItem_whenItemNotFound() {
    CreateOrderItemRequest request = new CreateOrderItemRequest();

    request.setOrderId(1L);
    request.setItemId(1L);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> orderItemService.createOrderItem(request)
    );
  }

  @Test
  void should_success_getOrderItemById() {
    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
    when(orderItemMapper.toResponse(orderItem)).thenReturn(response);

    OrderItemResponse result = orderItemService.getOrderItemById(1L);

    assertEquals(1L, result.getId());
  }

  @Test
  void should_throwException_getOrderItemById_whenOrderNotFound() {
    when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderItemService.getOrderItemById(1L)
    );
  }

  @Test
  void should_success_getAllOrderItems() {
    when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
    when(orderItemMapper.toResponse(orderItem)).thenReturn(response);

    List<OrderItemResponse> result = orderItemService.getAllOrderItems();

    assertEquals(1, result.size());
  }

  @Test
  void should_success_updateOrderItem_updateQuantity() {
    OrderItemRequest request =
        new OrderItemRequest();

    request.setQuantity(5);

    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
    when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
    when(orderItemMapper.toResponse(any(OrderItem.class))).thenReturn(response);

    OrderItemResponse result = orderItemService.updateOrderItem(1L, request);

    assertNotNull(result);
    verify(orderItemRepository).save(orderItem);
  }

  @Test
  void should_success_updateOrderItem_updateItemAndQuantity() {
    Item newItem = Item.builder()
        .id(2L)
        .name("Cable")
        .build();

    OrderItemRequest request = new OrderItemRequest();

    request.setItemId(2L);
    request.setQuantity(3);

    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
    when(itemRepository.findById(2L)).thenReturn(Optional.of(newItem));
    when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
    when(orderItemMapper.toResponse(any(OrderItem.class))).thenReturn(response);

    orderItemService.updateOrderItem(1L, request);

    assertEquals(newItem, orderItem.getItem());
    verify(itemRepository).findById(2L);
  }

  @Test
  void should_throwException_updateOrderItem_whenOrderItemNotFound() {
    OrderItemRequest request = new OrderItemRequest();

    request.setQuantity(5);

    when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderItemService.updateOrderItem(1L, request)
    );
  }

  @Test
  void should_throwException_updateOrderItem_whenQuantityInvalid() {
    OrderItemRequest request = new OrderItemRequest();

    request.setQuantity(0);

    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

    assertThrows(InvalidOrderQuantityException.class,
        () -> orderItemService.updateOrderItem(1L, request)
    );
  }

  @Test
  void should_throwException_updateOrderItem_whenNewItemNotFound() {
    OrderItemRequest request = new OrderItemRequest();

    request.setItemId(2L);
    request.setQuantity(1);

    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
    when(itemRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> orderItemService.updateOrderItem(1L, request)
    );
  }

  @Test
  void should_success_deleteOrderItem() {
    when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));

    orderItemService.deleteOrderItem(1L);

    verify(orderItemRepository).delete(orderItem);
  }

  @Test
  void should_throwException_deleteOrderItem_whenNotFound() {
    when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderItemService.deleteOrderItem(1L)
    );
  }
}
