package com.innowise.orderservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {

  @NotNull
  private Long itemId;

  @NotNull
  @Positive(message = "Quantity must be a positive number!")
  private Integer quantity;
}
