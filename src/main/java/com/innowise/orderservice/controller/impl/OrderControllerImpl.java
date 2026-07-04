package com.innowise.orderservice.controller.impl;

import com.innowise.orderservice.controller.OrderController;
import com.innowise.orderservice.model.dto.request.OrderRequest;
import com.innowise.orderservice.model.dto.response.OrderResponse;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
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

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

  private final OrderService orderService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequest));
  }

  @GetMapping("/{id}")
  @PostAuthorize("hasRole('ADMIN') or returnObject.body.userId.toString() == authentication.principal.claims['id']")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<OrderResponse>> getAllOrders(
      @RequestParam(required = false) @DateTimeFormat Instant createdFrom,
      @RequestParam(required = false) @DateTimeFormat Instant createdTo,
      @RequestParam(required = false) List<OrderStatus> statuses, Pageable pageable) {
    return ResponseEntity.ok(orderService.getAllOrders(createdFrom, createdTo, statuses, pageable));
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN') or authentication.principal == #userId")
  public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
      @Valid @RequestBody OrderRequest orderRequest) {
    return ResponseEntity.ok(orderService.updateOrder(id, orderRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
