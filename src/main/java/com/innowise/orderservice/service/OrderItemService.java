package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.CreateOrderItemRequest;
import com.innowise.orderservice.model.dto.request.OrderItemRequest;
import com.innowise.orderservice.model.dto.response.OrderItemResponse;
import java.util.List;

/**
 * Service interface defining the business contract for managing specific order items.
 * Provides methods for order-item creation, finding, updates, and removal.
 */
public interface OrderItemService {

  /**
   * Adds a new item position within an existing customer order.
   *
   * @param orderItemCreateRequest the data transfer object containing the target order ID, item ID, and quantity.
   * @return the created OrderItemResponse enriched with its ID and relations.
   */
  OrderItemResponse createOrderItem(CreateOrderItemRequest orderItemCreateRequest);

  /**
   * Finds the details of a specific order item by its ID.
   *
   * @param orderItemId the unique identifier (Long) of the order item record to retrieve.
   * @return the OrderItemResponse matching the given identifier.
   */
  OrderItemResponse getOrderItemById(Long orderItemId);

  /**
   * Finds a list of all order items registered across all application orders.
   *
   * @return a List of OrderItemResponse objects representing all registered positions.
   */
  List<OrderItemResponse> getAllOrderItems();

  /**
   * Updates the properties of an existing order item by its ID.
   *
   * @param orderItemId the unique identifier (Long) of the order item to modify.
   * @param orderItemUpdateRequest containing updated parameter values.
   * @return the updated OrderItemResponse.
   */
  OrderItemResponse updateOrderItem(Long orderItemId, OrderItemRequest orderItemUpdateRequest);

  /**
   * Permanently removes an item position from its associated order in the database by its ID.
   *
   * @param orderItemId the unique identifier (Long) of the order item to delete.
   */
  void deleteOrderItem(Long orderItemId);
}
