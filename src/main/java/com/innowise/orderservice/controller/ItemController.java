package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * REST controller interface providing API endpoints contract for managing items (products) in the
 * order service. Handles the creation, finding, updating, and deletion of item records. URL prefix:
 * /api/v1/items.
 */
public interface ItemController {

  /**
   * Creates a new item in the application. URL: /api/v1/items with POST method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input details are validated automatically before processing.
   *
   * @param itemRequest the ItemRequest containing name and price of the item to create.
   * @return ResponseEntity containing the created ItemResponse (id, name, price, deleted) with HTTP
   * status 201 (Created).
   */
  ResponseEntity<ItemResponse> createItem(ItemRequest itemRequest);

  /**
   * Finds details of a specific item by its ID. URL: /api/v1/items/{id} with GET method.
   *
   * <p>Access is permitted for authorized users with either 'ADMIN' or 'USER' roles.
   *
   * @param id the unique identifier (Long) of the item to find.
   * @return ResponseEntity containing the requested ItemResponse (id, name, price, deleted).
   */
  ResponseEntity<ItemResponse> getItemById(Long id);

  /**
   * Finds a full list of all available items in the application. URL: /api/v1/items with GET
   * method.
   *
   * <p>Access is permitted for authorized users with either 'ADMIN' or 'USER' roles.
   *
   * @return ResponseEntity containing a List of ItemResponse (id, name, price, deleted) objects.
   */
  ResponseEntity<List<ItemResponse>> getAllItems();

  /**
   * Updates an existing item's information by its ID. URL: /api/v1/items/{id} with PUT method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input payload is validated automatically before updating the entity state.
   *
   * @param id          the unique identifier (Long) of the item to update.
   * @param itemRequest containing modified values.
   * @return ResponseEntity containing the updated ItemResponse (id, name, price, deleted).
   */
  ResponseEntity<ItemResponse> updateItem(Long id, ItemRequest itemRequest);

  /**
   * Permanently deletes an item from the application by its ID. URL: /api/v1/items/{id} with DELETE
   * method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the item to delete.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  ResponseEntity<Void> deleteItem(Long id);
}