package com.innowise.orderservice.model.dto.request;

import com.innowise.orderservice.model.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

  @NotNull
  private UUID userId;

  @NotNull
  private OrderStatus orderStatus;

  @NotNull
  @Positive(message = "Total price must be a positive number!")
  @Digits(integer = 10, fraction = 2)
  private BigDecimal totalPrice;

  @NotNull
  @Size(min = 1)
  @Valid
  private List<OrderItemRequest> orderItems;
}
