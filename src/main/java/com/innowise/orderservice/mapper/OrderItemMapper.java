package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(target = "itemId", ignore = true)
  @Mapping(target = "orderId", ignore = true)
  OrderItemResponse toResponse(OrderItem save);
}
