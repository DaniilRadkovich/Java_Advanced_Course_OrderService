package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import com.innowise.orderservice.service.OrderItemService;
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
 * REST controller providing API endpoints for managing specific order items. Handles the creation,
 * finding, updating, and removal of items linked to customer orders.
 */
@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemController {

  private final OrderItemService orderItemService;

  /**
   * Adds a new item entry to an existing order.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input payload structure is validated automatically before processing.
   *
   * @param orderItemRequest containing the order ID, item ID, and quantity.
   * @return ResponseEntity containing the created OrderItemResponse with HTTP status 201 (Created).
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> createItem(
      @Valid @RequestBody CreateOrderItemRequest orderItemRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderItemService.createOrderItem(orderItemRequest));
  }

  /**
   * Finds the details of a specific order item by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order item record.
   * @return ResponseEntity containing the requested OrderItemResponse.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
    return ResponseEntity.ok(orderItemService.getOrderItemById(id));
  }

  /**
   * Finds a list of all order items registered in the application.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @return ResponseEntity containing a List of OrderItemResponse objects.
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
    return ResponseEntity.ok(orderItemService.getAllOrderItems());
  }

  /**
   * Updates an existing order item's parameters by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Form data fields are validated automatically before updating the resource state.
   *
   * @param id the unique identifier (Long) of the order item to modify.
   * @param orderItemRequest containing updated quantity or relationship values.
   * @return ResponseEntity containing the modified OrderItemResponse.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> updateOrderItem(@PathVariable Long id,
      @Valid @RequestBody OrderItemRequest orderItemRequest) {
    return ResponseEntity.ok(orderItemService.updateOrderItem(id, orderItemRequest));
  }

  /**
   * Permanently removes an order item from its associated order in the database by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order item to delete.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
    orderItemService.deleteOrderItem(id);
    return ResponseEntity.noContent().build();
  }
}
