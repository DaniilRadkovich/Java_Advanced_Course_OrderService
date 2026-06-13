package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import com.innowise.orderservice.model.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

  @Mapping(target = "deleted", ignore = true)
  ItemResponse toResponse(Item item);

  @Mapping(target = "id", ignore = true)
  Item toEntity(ItemRequest itemRequest);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromRequest(ItemRequest itemUpdateRequest, @MappingTarget Item item);
}
