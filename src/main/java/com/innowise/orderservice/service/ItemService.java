package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import java.util.List;


public interface ItemService {

  ItemResponse createItem(ItemRequest itemCreateRequest);

  ItemResponse getItemById(Long itemId);

  List<ItemResponse> getAllItems();

  ItemResponse updateItem(Long itemId, ItemRequest itemUpdateRequest);

  void deleteItem(Long itemId);
}
