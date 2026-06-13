package com.innowise.orderservice.intergationtest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.BaseIntegrationTest;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderIntegrationTest extends BaseIntegrationTest {

  private static final UUID USER_ID = UUID.fromString("bc32a678-e3c9-49c7-8be7-c1926f69375f");

  private void mockUserService() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlMatching("/api/v1/users/.*"))
            .willReturn(okJson("""
                {
                  "id":"bc32a678-e3c9-49c7-8be7-c1926f69375f",
                  "name":"Marry",
                  "surname":"Jane",
                  "email":"jane@mail.com",
                  "active":true
                }
                """)
            )
    );
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_createOrder() throws Exception {
    mockUserService();

    Item item = itemRepository.save(
        Item.builder()
            .name("iPad")
            .price(BigDecimal.valueOf(100))
            .build());

    OrderItemRequest itemRequest = new OrderItemRequest();

    itemRequest.setItemId(item.getId());
    itemRequest.setQuantity(2);

    OrderRequest request = new OrderRequest();

    request.setUserId(USER_ID);
    request.setOrderItems(List.of(itemRequest));
    request.setOrderStatus(OrderStatus.NEW);
    request.setTotalPrice(BigDecimal.valueOf(200));

    mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user.name")
            .value("Marry"))
        .andExpect(jsonPath("$.totalPrice")
            .value(200));

    assertEquals(1, orderRepository.count());
    verify(getRequestedFor(urlMatching("/api/v1/users/.*")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_getOrderById() throws Exception {
    mockUserService();

    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.TEN)
        .build());

    mockMvc.perform(get("/api/v1/orders/{id}", order.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.name")
            .value("Marry"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_getAllOrders() throws Exception {
    mockUserService();

    orderRepository.save(Order.builder()
        .userId(USER_ID)
        .status(OrderStatus.NEW)
        .totalPrice(BigDecimal.TEN)
        .build());

    mockMvc.perform(get("/api/v1/orders")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()")
            .value(1));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_updateOrder() throws Exception {
    mockUserService();

    Item item = itemRepository.save(Item.builder()
        .name("iPad")
        .price(BigDecimal.valueOf(100))
        .build());

    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .totalPrice(BigDecimal.valueOf(100))
        .status(OrderStatus.NEW)
        .build());

    OrderItemRequest itemRequest = new OrderItemRequest();

    itemRequest.setItemId(item.getId());
    itemRequest.setQuantity(3);

    OrderRequest request = new OrderRequest();

    request.setUserId(USER_ID);
    request.setOrderStatus(OrderStatus.IN_PROGRESS);
    request.setOrderItems(List.of(itemRequest));
    request.setTotalPrice(BigDecimal.valueOf(300));

    mockMvc.perform(put("/api/v1/orders/{id}", order.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderStatus")
            .value("IN_PROGRESS"))
        .andExpect(jsonPath("$.totalPrice")
            .value(300));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_deleteOrder() throws Exception {
    Order order = orderRepository.save(Order.builder()
        .userId(USER_ID)
        .totalPrice(BigDecimal.valueOf(100))
        .status(OrderStatus.NEW)
        .build());

    mockMvc.perform(delete("/api/v1/orders/{id}", order.getId()))
        .andExpect(status().isNoContent());

    assertTrue(orderRepository.findById(order.getId()).isEmpty());
  }

  @Test
  @WithMockUser(roles = "USER")
  void should_returnForbidden_deleteOrder() throws Exception {
    mockMvc.perform(delete("/api/v1/orders/1"))
        .andExpect(status().isForbidden());
  }

  @Test
  void should_returnAnUnauthorized_getOrders() throws Exception {
    mockMvc.perform(get("/api/v1/orders"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_createOrder_whenUserServiceUnavailable() throws Exception {
    WireMock.stubFor(WireMock.get(urlMatching("/api/v1/users/.*"))
        .willReturn(serverError()));

    Item item = itemRepository.save(Item.builder()
        .name("iPad")
        .price(BigDecimal.TEN)
        .build());

    OrderItemRequest itemRequest = new OrderItemRequest();

    itemRequest.setItemId(item.getId());
    itemRequest.setQuantity(1);

    OrderRequest request = new OrderRequest();

    request.setUserId(USER_ID);
    request.setOrderItems(List.of(itemRequest));
    request.setOrderStatus(OrderStatus.NEW);
    request.setTotalPrice(BigDecimal.valueOf(10));

    mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
}
