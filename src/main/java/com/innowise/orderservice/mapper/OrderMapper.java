package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "status", target = "orderStatus")
  @Mapping(target = "user", ignore = true)
  OrderResponse toResponse(Order order);
}
