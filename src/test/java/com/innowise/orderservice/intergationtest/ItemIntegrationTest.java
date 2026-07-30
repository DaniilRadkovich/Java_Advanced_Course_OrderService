package com.innowise.orderservice.intergationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.BaseIntegrationTest;
import com.innowise.orderservice.model.dto.request.ItemRequest;
import com.innowise.orderservice.model.entity.Item;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemIntegrationTest extends BaseIntegrationTest {

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_createItem() throws Exception {
    ItemRequest request = new ItemRequest();

    request.setName("iPad");
    request.setPrice(BigDecimal.valueOf(1000));

    mockMvc.perform(post("/api/v1/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name")
            .value("iPad"));

    assertEquals(1, itemRepository.count()
    );
  }

  @Test
  @WithMockUser(roles = "USER")
  void should_success_getItemById() throws Exception {
    Item item = Item.builder()
        .name("iPad")
        .price(BigDecimal.valueOf(1000))
        .build();

    item = itemRepository.save(item);

    mockMvc.perform(get("/api/v1/items/{id}", item.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name")
            .value("iPad"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void should_success_getAllItems() throws Exception {
    itemRepository.save(
        Item.builder()
            .name("iPad")
            .price(BigDecimal.TEN)
            .build());

    itemRepository.save(
        Item.builder()
            .name("Cable")
            .price(BigDecimal.ONE)
            .build());

    mockMvc.perform(get("/api/v1/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()")
            .value(2));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_updateItem() throws Exception {
    Item item = itemRepository.save(
        Item.builder()
            .name("Old")
            .price(BigDecimal.TEN)
            .build());

    ItemRequest request = new ItemRequest();

    request.setName("New");
    request.setPrice(BigDecimal.valueOf(100));

    mockMvc.perform(put("/api/v1/items/{id}", item.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name")
            .value("New"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void should_success_deleteItem() throws Exception {
    Item item = itemRepository.save(
        Item.builder()
            .name("iPad")
            .price(BigDecimal.TEN)
            .build());

    mockMvc.perform(delete("/api/v1/items/{id}", item.getId()))
        .andExpect(status().isNoContent());

    assertFalse(itemRepository.findById(item.getId()).isPresent());
  }

  @Test
  void should_getUnauthorized_createItem_whenAnonymous() throws Exception {
    ItemRequest request = new ItemRequest();

    request.setName("iPad");

    mockMvc.perform(post("/api/v1/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
