package com.innowise.orderservice.intergationtest;

import com.innowise.orderservice.BaseIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderItemIntegrationTest extends BaseIntegrationTest {

  private static final UUID USER_ID = UUID.randomUUID();

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_createOrderItem() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.ZERO)
        .build());

    Item item = itemRepository.save(Item.builder()
        .name("Watch")
        .price(BigDecimal.valueOf(100))
        .build());

    CreateOrderItemRequest request = new CreateOrderItemRequest();

    request.setOrderId(order.getId());
    request.setItemId(item.getId());
    request.setQuantity(2);

    mockMvc.perform(post("/api/v1/order-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantity").value(2));

    assertEquals(1, orderItemRepository.count()
    );
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_getOrderItemById() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.ZERO)
        .build());

    Item item = itemRepository.save(Item.builder()
        .name("Watch")
        .price(BigDecimal.TEN)
        .build());

    OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
        .order(order)
        .item(item)
        .quantity(2)
        .build());

    mockMvc.perform(get("/api/v1/order-items/{id}", orderItem.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantity").value(2));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_getAllOrderItems() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.ZERO)
        .build());

    Item item = itemRepository.save(Item.builder()
        .name("Watch")
        .price(BigDecimal.TEN)
        .build());

    orderItemRepository.save(OrderItem.builder()
        .order(order)
        .item(item)
        .quantity(1)
        .build());

    orderItemRepository.save(OrderItem.builder()
        .order(order)
        .item(item)
        .quantity(2)
        .build());

    mockMvc.perform(get("/api/v1/order-items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_updateOrderItem_updateQuantity() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.ZERO)
        .build());

    Item item = itemRepository.save(Item.builder()
        .name("Watch")
        .price(BigDecimal.TEN)
        .build());

    OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
        .order(order)
        .item(item)
        .quantity(1)
        .build());

    OrderItemRequest request = new OrderItemRequest();

    request.setItemId(item.getId());
    request.setQuantity(5);

    mockMvc.perform(put("/api/v1/order-items/{id}", orderItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantity").value(5));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_deleteOrderItem() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.ZERO)
        .build());

    Item item = itemRepository.save(Item.builder()
        .name("Watch")
        .price(BigDecimal.TEN)
        .build());

    OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
        .order(order)
        .item(item)
        .quantity(1)
        .build());

    mockMvc.perform(delete("/api/v1/order-items/{id}", orderItem.getId()))
        .andExpect(status().isNoContent());

    assertTrue(orderItemRepository.findById(orderItem.getId()).isEmpty()
    );
  }

  @Test
  @WithMockUser(roles = "USER")
  void should_returnForbidden_getOrderItems() throws Exception {
    mockMvc.perform(get("/api/v1/order-items")).andExpect(status().isForbidden());
  }

  @Test
  void should_returnUnauthorized_getOrderItems() throws Exception {
    mockMvc.perform(get("/api/v1/order-items")).andExpect(status().isUnauthorized());
  }
}