package com.innowise.orderservice.messaging;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {

  private Long orderId;
  private UUID userId;
  private BigDecimal totalPrice;
}
