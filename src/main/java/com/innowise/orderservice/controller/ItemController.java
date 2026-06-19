package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.dto.response.ItemResponse;
import com.innowise.orderservice.service.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller providing API endpoints for managing items (products) in the order service.
 * Handles the creation, finding, updating, and deletion of item records.
 */
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  /**
   * Creates a new item in the application.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input details are validated automatically before processing.
   *
   * @param itemRequest the ItemRequest containing name and price of the item to create.
   * @return ResponseEntity containing the created ItemResponse (id, name, price, deleted) with HTTP
   * status 201 (Created).
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest itemRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemRequest));
  }

  /**
   * Finds details of a specific item by its ID.
   *
   * <p>Access is permitted for authorized users with either 'ADMIN' or 'USER' roles.
   *
   * @param id the unique identifier (Long) of the item to find.
   * @return ResponseEntity containing the requested ItemResponse (id, name, price, deleted).
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
    return ResponseEntity.ok(itemService.getItemById(id));
  }

  /**
   * Finds a full list of all available items in the application.
   *
   * <p>Access is permitted for authorized users with either 'ADMIN' or 'USER' roles.
   *
   * @return ResponseEntity containing a List of ItemResponse (id, name, price, deleted) objects.
   */
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<List<ItemResponse>> getAllItems() {
    return ResponseEntity.ok(itemService.getAllItems());
  }

  /**
   * Updates an existing item's information by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input payload is validated automatically before updating the entity state.
   *
   * @param id the unique identifier (Long) of the item to update.
   * @param itemRequest containing modified values.
   * @return ResponseEntity containing the updated ItemResponse (id, name, price, deleted).
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id,
      @Valid @RequestBody ItemRequest itemRequest) {
    return ResponseEntity.ok(itemService.updateItem(id, itemRequest));
  }

  /**
   * Permanently deletes an item from the application by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the item to delete.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
    itemService.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}