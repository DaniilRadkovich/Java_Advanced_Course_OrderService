package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

  @Mapping(source = "order.id", target = "orderId")
  @Mapping(source = "item.id", target = "itemId")
  OrderItemResponse toResponse(OrderItem save);
}
