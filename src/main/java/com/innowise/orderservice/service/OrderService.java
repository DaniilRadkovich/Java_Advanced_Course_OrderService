package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface defining the business contract for managing customer orders. Provides methods
 * for creation, searching/filtering, updating and deletion of orders.
 */
public interface OrderService {

  /**
   * Creates a new customer order in the application.
   *
   * @param orderCreateRequest the data transfer object containing initial order details and items.
   * @return the created OrderResponse enriched with its generated identifier and initial state.
   */
  OrderResponse createOrder(OrderRequest orderCreateRequest);

  /**
   * Finds the comprehensive details of a specific order by its ID.
   *
   * @param orderId the unique identifier (Long) of the order to find.
   * @return the OrderResponse matching the given identifier.
   */
  OrderResponse getOrderById(Long orderId);

  /**
   * Finds a paginated and sorted page of orders filtered by a registration date range.
   *
   * @param from optional lower boundary timestamp (Instant) for the order creation date.
   * @param to optional upper boundary timestamp (Instant) for the order creation date.
   * @param statuses optional list of OrderStatus requirements to filter by.
   * @param page pagination and sorting parameters.
   * @return a Page of OrderResponse matching the requested filters.
   */
  Page<OrderResponse> getAllOrders(Instant from, Instant to, List<OrderStatus> statuses,
      Pageable page);

  /**
   * Finds all orders associated with a specific customer ID.
   *
   * @param userId the unique identifier (UUID) of the user who placed the orders.
   * @return a List of OrderResponse orders belonging to the user.
   */
  List<OrderResponse> getOrdersByUserId(UUID userId);

  /**
   * Modifies an existing order's information.
   *
   * @param orderId      the unique identifier (Long) of the order to update.
   * @param orderRequest payload containing updated properties.
   * @return the updated OrderResponse.
   */
  OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);

  /**
   * Permanently deletes an order from the database by its ID.
   *
   * @param orderId the unique identifier (Long) of the order to remove.
   */
  void deleteOrder(Long orderId);

  /**
   * Modifies an existing order's status from Kafka.
   *
   * @param orderId      the unique identifier (Long) of the order to update.
   * @param status status of the order.
   */
  void updateOrderStatusFromKafka(Long orderId, OrderStatus status);
}
