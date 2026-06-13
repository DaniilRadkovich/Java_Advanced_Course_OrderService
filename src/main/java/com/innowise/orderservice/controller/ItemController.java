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

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest itemRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemRequest));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
    return ResponseEntity.ok(itemService.getItemById(id));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<List<ItemResponse>> getAllItems() {
    return ResponseEntity.ok(itemService.getAllItems());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id,
      @Valid @RequestBody ItemRequest itemRequest) {
    return ResponseEntity.ok(itemService.updateItem(id, itemRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
    itemService.deleteItem(id);
    return ResponseEntity.noContent().build();
  }
}