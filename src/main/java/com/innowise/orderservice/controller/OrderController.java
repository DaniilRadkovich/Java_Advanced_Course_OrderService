package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller providing API endpoints for managing customer orders. Handles order creation,
 * finding, search/filtering, updating, and deletion.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  /**
   * Creates a new order in the application.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Input details are validated automatically before processing.
   *
   * @param orderRequest containing details of the order to register.
   * @return ResponseEntity containing the created OrderResponse (full information about order and
   * user) with HTTP status 201 (Created).
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequest));
  }

  /**
   * Finds the details of a specific order by its ID.
   *
   * <p>Access is permitted for users with the 'ADMIN' role, or if the requester's
   * ID matches the requested order ID.
   *
   * @param id the unique identifier (Long) of the order to find.
   * @return ResponseEntity containing the requested OrderResponse (full information about order and
   * user).
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or authentication.principal == #id")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  /**
   * Finds a paginated list of orders filtered by optional date ranges and status codes.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param createdFrom optional lower boundary timestamp (Instant) for the order creation date.
   * @param createdTo optional upper boundary timestamp (Instant) for the order creation date.
   * @param statuses optional list of OrderStatus filters to match against.
   * @param pageable pagination and sorting configuration parameters.
   * @return ResponseEntity containing a Page of OrderResponse objects matching the filters.
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<OrderResponse>> getAllOrders(
      @RequestParam(required = false) @DateTimeFormat Instant createdFrom,
      @RequestParam(required = false) @DateTimeFormat Instant createdTo,
      @RequestParam(required = false) List<OrderStatus> statuses, Pageable pageable) {
    return ResponseEntity.ok(orderService.getAllOrders(createdFrom, createdTo, statuses, pageable));
  }

  /**
   * Updates an existing order's information.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   * Payload data fields are validated automatically before updating the resource.
   *
   * @param id the unique identifier (Long) of the order to modify.
   * @param orderRequest containing the updated order details.
   * @return ResponseEntity containing the modified OrderResponse.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
      @Valid @RequestBody OrderRequest orderRequest) {
    return ResponseEntity.ok(orderService.updateOrder(id, orderRequest));
  }

  /**
   * Permanently deletes an order from the application database by its ID.
   *
   * <p>Access is allowed exclusively to users with the 'ADMIN' role.
   *
   * @param id the unique identifier (Long) of the order to remove.
   * @return ResponseEntity with HTTP status 204 (No Content).
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
