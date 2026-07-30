package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.InvalidOrderDataException;
import com.innowise.orderservice.exception.InvalidOrderQuantityException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.exception.ProductNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.service.UserService;
import com.innowise.orderservice.specification.OrderSpecification;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final String ORDER_NOT_FOUND = "Order not found! ID: ";

  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final OrderMapper orderMapper;
  private final OrderItemMapper orderItemMapper;
  private final UserService userService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public OrderResponse createOrder(OrderRequest orderRequest) {
    if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
      throw new InvalidOrderDataException("Order items cannot be empty!");
    }

    Order order = orderMapper.toEntity(orderRequest);

    List<OrderItem> items = orderRequest.getOrderItems().stream().map(itemRequest -> {
          Item item = itemRepository.findById(itemRequest.getItemId())
              .orElseThrow(() -> new ProductNotFoundException("Item not found!"));

          if (itemRequest.getQuantity() <= 0) {
            throw new InvalidOrderQuantityException("Quantity must be greater than 0!");
          }

          OrderItem orderItem = orderItemMapper.toEntity(itemRequest, item);
          orderItem.setOrder(order);

          return orderItem;
        })
        .toList();

    order.setOrderItems(items);

    BigDecimal totalPrice = calculateTotalPrice(items);
    order.setTotalPrice(totalPrice);

    Order savedOrder = orderRepository.save(order);

    applicationEventPublisher.publishEvent(savedOrder);

    log.info(
        "****KAFKA**** Order {} successfully created. Event scheduled for publishing after transaction commit. ****KAFKA****",
        savedOrder.getId()
    );

    return addUserInfo(savedOrder);
  }

  @Override
  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND + orderId));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin) {
      String currentUserId = authentication.getName();

      if (!order.getUserId().toString().equals(currentUserId)) {
        throw new AccessDeniedException("No access to this order!");
      }
    }
    return addUserInfo(order);
  }

  @Override
  public Page<OrderResponse> getAllOrders(Instant from, Instant to, List<OrderStatus> statuses,
      Pageable page) {
    Specification<Order> specification = Specification.allOf(
        OrderSpecification.createdFromTo(from, to), OrderSpecification.hasStatuses(statuses));

    Page<Order> orders = orderRepository.findAll(specification, page);
    return orders.map(this::addUserInfo);
  }

  @Override
  public List<OrderResponse> getOrdersByUserId(UUID userId) {
    List<Order> userOrders = orderRepository.findAllByUserId(userId);

    return userOrders.stream().map(order -> {
      OrderResponse orderResponse = orderMapper.toResponse(order);
      UserDto userDto = userService.getUserById(orderResponse.getUserId());
      orderResponse.setUser(userDto);
      return orderResponse;
    }).toList();
  }

  @Override
  @Transactional
  public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND + orderId));

    if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
      throw new InvalidOrderDataException("Order items cannot be empty!");
    }

    order.setStatus(orderRequest.getOrderStatus());
    order.setUpdatedAt(Instant.now());

    List<OrderItem> updatedItems = orderRequest.getOrderItems().stream().map(
        orderItemRequest -> {
          if (orderItemRequest.getQuantity() <= 0) {
            throw new InvalidOrderQuantityException("Quantity must be greater than 0!");
          }
          Item item = itemRepository.findById(orderItemRequest.getItemId())
              .orElseThrow(() -> new ProductNotFoundException("Item not found!"));

          OrderItem orderItem = orderItemMapper.toEntity(orderItemRequest, item);
          orderItem.setOrder(order);

          return orderItem;
        }
    ).toList();

    if (order.getOrderItems() != null) {
      order.getOrderItems().clear();
      order.getOrderItems().addAll(updatedItems);
    } else {
      order.setOrderItems(updatedItems);
    }

    BigDecimal totalPrice = calculateTotalPrice(updatedItems);
    order.setTotalPrice(totalPrice);

    Order savedOrder = orderRepository.save(order);
    return addUserInfo(savedOrder);
  }

  @Override
  @Transactional
  public void deleteOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND + orderId));
    orderRepository.delete(order);
  }

  @Transactional
  public void updateOrderStatusFromKafka(Long orderId, OrderStatus status) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND + orderId));
    order.setStatus(status);
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);
    log.info(
        "Order with id: {} status successfully updated its status: {} from OrderService with Kafka!",
        orderId,
        status);
  }

  private OrderResponse addUserInfo(Order order) {
    OrderResponse orderResponse = orderMapper.toResponse(order);
    try {
      UserDto userDto = userService.getUserById(orderResponse.getUserId());
      orderResponse.setUser(userDto);
    } catch (Exception e) {
      log.error("Error with getting user from UserService: {}", e.getMessage());
      orderResponse.setUser(UserDto.builder()
          .id(orderResponse.getUserId())
          .name("Unknown")
          .surname("Unknown")
          .build());
    }
    return orderResponse;
  }

  private BigDecimal calculateTotalPrice(List<OrderItem> items) {
    return items.stream()
        .map(orderItem -> orderItem.getItem().getPrice()
            .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
