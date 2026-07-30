package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * REST controller interface providing API endpoints contract for managing customer orders. Handles
 * order creation, finding, search/filtering, updating, and deletion. URL prefix: /api/v1/orders.
 */
public interface OrderController {

  /**
   * Creates a new order in the application. URL: /api/v1/orders with POST method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input details are validated automatically before processing.
   *
   * @param orderRequest containing details of the order to register.
   * @return ResponseEntity containing the created OrderResponse (full information about order and
   * user) with HTTP status 201 (Created).
   */
  ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest);

  /**
   * Finds the details of a specific order by its ID. URL: /api/v1/orders/{id} with GET method.
   *
   * <p>Access is permitted for users with the 'ADMIN' role, or if the requester's
   * ID matches the requested order ID.
   *
   * @param id the unique identifier (Long) of the order to find.
   * @return ResponseEntity containing the requested OrderResponse (full information about order and
   * user).
   */
  ResponseEntity<OrderResponse> getOrderById(Long id);

  /**
   * Finds a paginated list of orders filtered by optional date ranges and status codes. URL:
   * /api/v1/orders with GET method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param createdFrom optional lower boundary timestamp (Instant) for the order creation date.
   * @param createdTo   optional upper boundary timestamp (Instant) for the order creation date.
   * @param statuses    optional list of OrderStatus filters to match against.
   * @param pageable    pagination and sorting configuration parameters.
   * @return ResponseEntity containing a Page of OrderResponse objects matching the filters.
   */
  ResponseEntity<Page<OrderResponse>> getAllOrders(Instant createdFrom, Instant createdTo,
      List<OrderStatus> statuses, Pageable pageable);

  /**
   * Finds the list of a specific order by user ID. URL: /api/v1/orders/user/{userId} with GET
   * method.
   *
   * <p>Access is permitted for users with the 'ADMIN' role, or if the requester's
   * ID matches the requested user ID.
   *
   * @param userId the unique identifier (UUID) of the user.
   * @return ResponseEntity containing the List of requested OrderResponse (full information about
   * order and user).
   */
  ResponseEntity<List<OrderResponse>> getOrdersByUserId(UUID userId);

  /**
   * Updates an existing order's information. URL: /api/v1/orders/{id} with PUT method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Payload data fields are validated automatically before updating the resource.
   *
   * @param id           the unique identifier (Long) of the order to modify.
   * @param orderRequest containing the updated order details.
   * @return ResponseEntity containing the modified OrderResponse.
   */
  ResponseEntity<OrderResponse> updateOrder(Long id, OrderRequest orderRequest);

  /**
   * Permanently deletes an order from the application database by its ID. URL: /api/v1/orders/{id}
   * with DELETE method.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order to remove.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  ResponseEntity<Void> deleteOrder(Long id);
}
