package com.innowise.orderservice.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.exception.InvalidItemDataException;
import com.innowise.orderservice.exception.ProductNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ItemMapper itemMapper;

  @InjectMocks
  private ItemServiceImpl itemService;

  private Item item;
  private ItemRequest itemRequest;
  private ItemResponse itemResponse;

  @BeforeEach
  void setUp() {
    item = new Item();
    item.setId(1L);
    item.setName("iPhone");

    itemRequest = new ItemRequest();
    itemRequest.setName("iPhone");

    itemResponse = new ItemResponse();
    itemResponse.setId(1L);
    itemResponse.setName("iPhone");
  }

  @Test
  void should_success_createItem() {
    when(itemRepository.existsByName("iPhone")).thenReturn(false);
    when(itemMapper.toEntity(itemRequest)).thenReturn(item);
    when(itemMapper.toResponse(item)).thenReturn(itemResponse);

    ItemResponse result = itemService.createItem(itemRequest);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(itemRepository).save(item);
  }

  @Test
  void should_throwException_createItem_whenItemExists() {
    when(itemRepository.existsByName("iPhone")).thenReturn(true);

    assertThrows(InvalidItemDataException.class, () -> itemService.createItem(itemRequest));
    verify(itemRepository, never()).save(any());
  }

  @Test
  void should_returnItem_getItemById() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(itemMapper.toResponse(item)).thenReturn(itemResponse);

    ItemResponse result = itemService.getItemById(1L);

    assertEquals(1L, result.getId());
    verify(itemRepository).findById(1L);
  }

  @Test
  void should_throwException_getItemById_whenItemNotFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> itemService.getItemById(1L));
  }

  @Test
  void should_returnItems_getAllItems() {
    when(itemRepository.findAll()).thenReturn(List.of(item));
    when(itemMapper.toResponse(item)).thenReturn(itemResponse);

    List<ItemResponse> result = itemService.getAllItems();

    assertEquals(1, result.size());
    assertEquals("iPhone", result.getFirst().getName());
  }

  @Test
  void should_success_updateItem() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(itemMapper.toResponse(item)).thenReturn(itemResponse);

    ItemResponse result = itemService.updateItem(1L, itemRequest);

    verify(itemMapper).updateEntityFromRequest(itemRequest, item);
    verify(itemRepository).save(item);
    assertNotNull(result);
  }

  @Test
  void should_throwException_updateItem_whenItemNotFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> itemService.updateItem(1L, itemRequest));
    verify(itemRepository, never()).save(any());
  }

  @Test
  void should_success_deleteItem() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    itemService.deleteItem(1L);

    verify(itemRepository).delete(item);
  }

  @Test
  void should_throwException_deleteItem_whenItemNotFound() {
    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> itemService.deleteItem(1L));
    verify(itemRepository, never()).delete(any());
  }
}
