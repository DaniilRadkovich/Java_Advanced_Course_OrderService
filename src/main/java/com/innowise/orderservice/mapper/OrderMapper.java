package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = OrderItemMapper.class
)
public interface OrderMapper {

  @Mapping(target = "status", source = "orderStatus")
  @Mapping(target = "orderItems", ignore = true)
  Order toEntity(OrderRequest orderRequest);

  @Mapping(source = "status", target = "orderStatus")
  @Mapping(target = "user", ignore = true)
  OrderResponse toResponse(Order order);
}
