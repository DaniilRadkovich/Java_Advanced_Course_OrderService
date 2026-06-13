package com.innowise.orderservice.model.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

  private Long id;
  private String name;
  private BigDecimal price;
  private boolean deleted;
}
