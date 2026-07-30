package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import java.util.List;

/**
 * Service interface defining the business contract for managing items (products) in the application.
 * Provides methods for item creation, finding, updates, and removal.
 */
public interface ItemService {

  /**
   * Creates and registers a new item in the application.
   *
   * @param itemCreateRequest the data transfer object containing new item parameters.
   * @return the created ItemResponse populated with its generated identifier.
   */
  ItemResponse createItem(ItemRequest itemCreateRequest);

  /**
   * Finds the details of a specific item by its unique identifier.
   *
   * @param itemId the unique identifier (Long) of the item to retrieve.
   * @return the ItemResponse matching the given identifier.
   */
  ItemResponse getItemById(Long itemId);

  /**
   * Finds a list of all items available in the system catalog.
   *
   * @return a List of ItemResponse objects representing all items.
   */
  List<ItemResponse> getAllItems();

  /**
   * Updates the properties of an existing item by its ID.
   *
   * @param itemId the unique identifier (Long) of the item to update.
   * @param itemUpdateRequest containing modified values.
   * @return the updated ItemResponse.
   */
  ItemResponse updateItem(Long itemId, ItemRequest itemUpdateRequest);

  /**
   * Removes an item from the application by its ID.
   *
   * @param itemId the unique identifier (Long) of the item to delete.
   */
  void deleteItem(Long itemId);
}
