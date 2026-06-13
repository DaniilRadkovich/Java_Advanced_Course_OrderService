package com.innowise.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ItemRepository itemRepository;

  @Autowired
  protected OrderRepository orderRepository;

  @Autowired
  protected OrderItemRepository orderItemRepository;

  @Container
  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine");

  @DynamicPropertySource
  static void configureProperties(
      DynamicPropertyRegistry registry) {

    registry.add(
        "spring.datasource.url",
        postgres::getJdbcUrl);

    registry.add(
        "spring.datasource.username",
        postgres::getUsername);

    registry.add(
        "spring.datasource.password",
        postgres::getPassword);
  }

  @BeforeEach
  void cleanDb() {
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
    itemRepository.deleteAll();
  }
}