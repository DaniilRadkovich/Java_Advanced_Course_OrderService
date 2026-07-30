package com.innowise.orderservice.model.dto.response;

import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

  private Long id;
  private UUID userId;
  private UserDto user;
  private OrderStatus orderStatus;
  private BigDecimal totalPrice;
  private boolean deleted;
  private Instant createdAt;
  private Instant updatedAt;
  private List<OrderItemResponse> orderItems;
}
