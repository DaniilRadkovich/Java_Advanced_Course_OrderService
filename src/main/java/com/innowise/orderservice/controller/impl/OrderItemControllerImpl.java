package com.innowise.orderservice.controller.impl;

import com.innowise.orderservice.controller.OrderItemController;
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

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemControllerImpl implements OrderItemController {

  private final OrderItemService orderItemService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> createItem(
      @Valid @RequestBody CreateOrderItemRequest orderItemRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderItemService.createOrderItem(orderItemRequest));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
    return ResponseEntity.ok(orderItemService.getOrderItemById(id));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
    return ResponseEntity.ok(orderItemService.getAllOrderItems());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<OrderItemResponse> updateOrderItem(@PathVariable Long id,
      @Valid @RequestBody OrderItemRequest orderItemRequest) {
    return ResponseEntity.ok(orderItemService.updateOrderItem(id, orderItemRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
    orderItemService.deleteOrderItem(id);
    return ResponseEntity.noContent().build();
  }
}
