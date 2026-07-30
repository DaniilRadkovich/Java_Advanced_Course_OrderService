package com.innowise.orderservice.unittest;

import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.service.UserService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

  private MockWebServer mockWebServer;
  private UserService userService;
  private SecurityContext securityContext;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    userService = new UserService(webClient);

    securityContext = Mockito.mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
    SecurityContextHolder.clearContext();
  }

  @Test
  void getUserById_Success_WithBearerToken() throws InterruptedException {
    UUID userId = UUID.randomUUID();
    String expectedToken = "blablabla-jwt-token";

    Authentication authentication = Mockito.mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getCredentials()).thenReturn(expectedToken);

    String jsonResponse = String.format(
        "{\"id\":\"%s\",\"name\":\"Sid\",\"surname\":\"Sidov\",\"email\":\"sidov@mail.com\",\"active\":true}",
        userId
    );
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.OK_200)
        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .setBody(jsonResponse));

    UserDto result = userService.getUserById(userId);

    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("Sid", result.getName());
    assertTrue(result.isActive());

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertEquals("/api/v1/users/" + userId, recordedRequest.getPath());
    assertEquals("Bearer " + expectedToken, recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void getUserById_Success_WithoutToken() throws InterruptedException {
    UUID userId = UUID.randomUUID();

    when(securityContext.getAuthentication()).thenReturn(null);

    String jsonResponse = "{\"id\":\"" + userId + "\",\"name\":\"Guest\"}";
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.OK_200)
        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .setBody(jsonResponse));

    UserDto result = userService.getUserById(userId);

    assertNotNull(result);
    assertEquals("Guest", result.getName());

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertNull(recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void getUserById_ServerError_ThrowsRuntimeException() {
    UUID userId = UUID.randomUUID();
    when(securityContext.getAuthentication()).thenReturn(null);

    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR_500)
        .setBody("Internal Server Error"));

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        userService.getUserById(userId)
    );
    assertTrue(exception.getMessage().contains("User service error: 500"));
  }
}
