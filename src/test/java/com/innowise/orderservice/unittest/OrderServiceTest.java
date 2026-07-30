package com.innowise.orderservice.unittest;

import com.innowise.orderservice.exception.InvalidOrderDataException;
import com.innowise.orderservice.exception.InvalidOrderQuantityException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.UserService;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private OrderItemMapper orderItemMapper;

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private OrderServiceImpl orderService;

  private UUID userId;
  private Item item;
  private Order order;
  private OrderRequest orderRequest;
  private OrderResponse orderResponse;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    item = Item.builder()
        .id(1L)
        .name("Laptop")
        .price(BigDecimal.valueOf(100))
        .build();

    OrderItemRequest itemRequest = new OrderItemRequest();
    itemRequest.setItemId(1L);
    itemRequest.setQuantity(2);

    orderRequest = new OrderRequest();
    orderRequest.setUserId(userId);
    orderRequest.setOrderItems(List.of(itemRequest));
    orderRequest.setOrderStatus(OrderStatus.NEW);

    order = Order.builder()
        .id(1L)
        .userId(userId)
        .status(OrderStatus.NEW)
        .build();

    orderResponse = new OrderResponse();
    orderResponse.setId(1L);
    orderResponse.setUserId(userId);

    userDto = UserDto.builder()
        .id(userId)
        .name("John")
        .build();
  }

  @Test
  void should_success_createOrder() {
    Order mockOrder = new Order();
    mockOrder.setOrderItems(new ArrayList<>());

    when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(mockOrder);

    OrderItem mockOrderItem = new OrderItem();
    mockOrderItem.setItem(item);
    mockOrderItem.setQuantity(4);
    when(orderItemMapper.toEntity(any(), any(Item.class))).thenReturn(mockOrderItem);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);
    when(userService.getUserById(userId)).thenReturn(userDto);
    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

    OrderResponse result = orderService.createOrder(orderRequest);

    assertNotNull(result);
    assertEquals(userDto, result.getUser());
    verify(orderRepository).save(any(Order.class));
    verify(eventPublisher).publishEvent(any(Order.class));
  }

  @Test
  void should_throwException_createOrder_whenItemsEmpty() {
    orderRequest.setOrderItems(List.of());

    assertThrows(InvalidOrderDataException.class,
        () -> orderService.createOrder(orderRequest)
    );
  }

  @Test
  void should_throwException_createOrder_whenItemNotFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> orderService.createOrder(orderRequest)
    );
  }

  @Test
  void should_throwException_createOrder_whenQuantityInvalid() {
    Long itemId = orderRequest.getOrderItems().getFirst().getItemId();

    Item mockItem = Item.builder()
        .id(itemId)
        .name("Test Item")
        .price(BigDecimal.TEN)
        .build();

    when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(mockItem));

    orderRequest.getOrderItems()
        .getFirst()
        .setQuantity(0);

    assertThrows(InvalidOrderQuantityException.class,
        () -> orderService.createOrder(orderRequest)
    );
  }

  @Test
  void should_success_getOrderById() {
    orderResponse.setUser(userDto);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(authentication.getName()).thenReturn(userId.toString());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toResponse(order)).thenReturn(orderResponse);
    when(userService.getUserById(userId)).thenReturn(userDto);

    OrderResponse result = orderService.getOrderById(1L);

    assertEquals(userDto, result.getUser());
  }

  @Test
  void should_throwException_getOrderById_whenNotFound() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
  }

  @Test
  void should_success_getOrdersByUserId() {
    when(orderRepository.findAllByUserId(userId)).thenReturn(List.of(order));
    when(orderMapper.toResponse(order)).thenReturn(orderResponse);
    when(userService.getUserById(userId)).thenReturn(userDto);

    List<OrderResponse> result = orderService.getOrdersByUserId(userId);

    assertEquals(1, result.size());
    assertEquals(userDto, result.getFirst().getUser());
  }

  @Test
  void should_success_updateOrder() {
    order.setOrderItems(new ArrayList<>());

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    OrderItem mockOrderItem = new OrderItem();
    mockOrderItem.setItem(item);
    mockOrderItem.setQuantity(4);
    when(orderItemMapper.toEntity(any(), any(Item.class))).thenReturn(mockOrderItem);
    when(orderRepository.save(any(Order.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
    when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);
    when(userService.getUserById(userId)).thenReturn(userDto);

    OrderResponse result = orderService.updateOrder(1L, orderRequest);

    assertNotNull(result);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void should_throwException_updateOrder_whenOrderNotFound() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderService.updateOrder(1L, orderRequest)
    );
  }

  @Test
  void should_throwException_updateOrder_whenItemsEmpty() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    orderRequest.setOrderItems(List.of());

    assertThrows(InvalidOrderDataException.class,
        () -> orderService.updateOrder(1L, orderRequest)
    );
  }

  @Test
  void should_success_deleteOrder() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    orderService.deleteOrder(1L);

    verify(orderRepository).delete(order);
  }

  @Test
  void should_throwException_deleteOrder_whenNotFound() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class,
        () -> orderService.deleteOrder(1L)
    );
  }

  @Test
  void should_success_createOrder_calculateTotalPriceCorrectly() {
    Order mockOrder = new Order();
    mockOrder.setOrderItems(new ArrayList<>());

    when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(mockOrder);

    OrderItem mockOrderItem = new OrderItem();
    mockOrderItem.setItem(item);
    mockOrderItem.setQuantity(1);
    when(orderItemMapper.toEntity(any(), any(Item.class))).thenReturn(mockOrderItem);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);
    when(userService.getUserById(userId)).thenReturn(userDto);
    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

    orderService.createOrder(orderRequest);

    verify(orderRepository).save(captor.capture());

    Order savedOrder = captor.getValue();

    assertEquals(BigDecimal.valueOf(100), savedOrder.getTotalPrice()
    );
    verify(eventPublisher).publishEvent(any(Order.class));
  }
}
