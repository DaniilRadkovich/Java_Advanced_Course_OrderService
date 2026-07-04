package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "item", source = "item")
  OrderItem toEntity(OrderItemRequest itemRequest, Item item);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", source = "order")
  @Mapping(target = "item", source = "item")
  OrderItem toEntity(CreateOrderItemRequest request, Order order, Item item);

  @Mapping(source = "order.id", target = "orderId")
  @Mapping(source = "item.id", target = "itemId")
  OrderItemResponse toResponse(OrderItem save);
}
