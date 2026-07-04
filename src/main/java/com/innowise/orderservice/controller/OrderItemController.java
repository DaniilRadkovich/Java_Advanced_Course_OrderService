package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * REST controller interface providing API endpoints contract for managing specific order items.
 * Handles the creation, finding, updating, and removal of items linked to customer orders. URL
 * prefix: /api/v1/order-items.
 */
public interface OrderItemController {

  /**
   * Adds a new item entry to an existing order. URL: /api/v1/order-items with POST method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input payload structure is validated automatically before processing.
   *
   * @param orderItemRequest containing the order ID, item ID, and quantity.
   * @return ResponseEntity containing the created OrderItemResponse with HTTP status 201 (Created).
   */
  ResponseEntity<OrderItemResponse> createItem(CreateOrderItemRequest orderItemRequest);

  /**
   * Finds the details of a specific order item by its ID. URL: /api/v1/order-items/{id} with GET
   * method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order item record.
   * @return ResponseEntity containing the requested OrderItemResponse.
   */
  ResponseEntity<OrderItemResponse> getOrderItemById(Long id);

  /**
   * Finds a list of all order items registered in the application. URL: /api/v1/order-items with
   * GET method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @return ResponseEntity containing a List of OrderItemResponse objects.
   */
  ResponseEntity<List<OrderItemResponse>> getAllOrderItems();

  /**
   * Updates an existing order item's parameters by its ID. URL: /api/v1/order-items/{id} with PUT
   * method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Form data fields are validated automatically before updating the resource state.
   *
   * @param id               the unique identifier (Long) of the order item to modify.
   * @param orderItemRequest containing updated quantity or relationship values.
   * @return ResponseEntity containing the modified OrderItemResponse.
   */
  ResponseEntity<OrderItemResponse> updateOrderItem(Long id, OrderItemRequest orderItemRequest);

  /**
   * Permanently removes an order item from its associated order in the database by its ID. URL:
   * /api/v1/order-items/{id} with DELETE method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order item to delete.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  ResponseEntity<Void> deleteOrderItem(Long id);
}
