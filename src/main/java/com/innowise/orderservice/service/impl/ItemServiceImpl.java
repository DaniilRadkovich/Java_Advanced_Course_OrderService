package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.InvalidItemDataException;
import com.innowise.orderservice.exception.ProductNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private static final String ITEM_NOT_FOUND = "Item not found! ID: ";

  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;

  @Override
  public ItemResponse createItem(ItemRequest itemRequest) {
    if (itemRepository.existsByName(itemRequest.getName())) {
      throw new InvalidItemDataException("Item already exists!");
    }
    Item item = itemMapper.toEntity(itemRequest);
    itemRepository.save(item);
    return itemMapper.toResponse(item);
  }

  @Override
  public ItemResponse getItemById(Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ProductNotFoundException(ITEM_NOT_FOUND + itemId));
    return itemMapper.toResponse(item);
  }

  @Override
  public List<ItemResponse> getAllItems() {
    List<Item> allItems = itemRepository.findAll();
    return allItems.stream().map(itemMapper::toResponse).toList();
  }

  @Override
  @Transactional
  public ItemResponse updateItem(Long itemId, ItemRequest itemUpdateRequest) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ProductNotFoundException(ITEM_NOT_FOUND + itemId));
    itemMapper.updateEntityFromRequest(itemUpdateRequest, item);
    itemRepository.save(item);
    return itemMapper.toResponse(item);
  }

  @Override
  @Transactional
  public void deleteItem(Long itemId) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new ProductNotFoundException(ITEM_NOT_FOUND + itemId));
    itemRepository.delete(item);
  }
}
