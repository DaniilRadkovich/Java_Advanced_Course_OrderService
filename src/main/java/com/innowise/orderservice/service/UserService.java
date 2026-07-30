package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final WebClient webClient;

  @CircuitBreaker(name = "user-service", fallbackMethod = "getUserByIdFallback")
  public UserDto getUserById(UUID userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String token = null;

    if (authentication != null && authentication.getCredentials() != null) {
      token = authentication.getCredentials().toString();
    }

    final String finalToken = token;

    return webClient.get().uri("/api/v1/users/{id}", userId).headers(
            headers -> {
              if (finalToken != null && !finalToken.isBlank()) {
                headers.setBearerAuth(finalToken);
              }
            })
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            response -> response.bodyToMono(String.class)
                .flatMap(body -> {
                  log.error("UserService returned {}: {}", response.statusCode(), body);
                  return Mono.error(
                      new RuntimeException("User service error: " + response.statusCode()));
                })
        )
        .bodyToMono(UserDto.class)
        .block();
  }


  private UserDto getUserByIdFallback(UUID userId, Throwable ex) {
    log.error("Fallback triggered for userId {}. Cause: {}", userId, ex.getMessage());
    return UserDto.builder()
        .id(userId)
        .name("Unknown")
        .surname("Unknown")
        .email("unknown@mail.com")
        .birthDate(null)
        .active(false)
        .build();
  }
}
