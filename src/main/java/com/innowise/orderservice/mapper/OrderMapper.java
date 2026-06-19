package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = OrderItemMapper.class
)
public interface OrderMapper {

  @Mapping(source = "status", target = "orderStatus")
  @Mapping(target = "user", ignore = true)
  OrderResponse toResponse(Order order);
}
