package com.innowise.orderservice.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

  @NotNull
  @NotBlank(message = "Name cannot be blank!")
  private String name;

  @NotNull(message = "Price is required!")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price cannot be negative!")
  private BigDecimal price;
}
