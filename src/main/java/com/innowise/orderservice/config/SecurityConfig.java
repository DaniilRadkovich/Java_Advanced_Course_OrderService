package com.innowise.orderservice.config;

import com.innowise.orderservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info")
            .permitAll()

            .requestMatchers(HttpMethod.POST, "/api/v1/items").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/items/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/items/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/items/**").hasAnyRole("ADMIN", "USER")

            .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("ADMIN", "USER")

            .requestMatchers(HttpMethod.POST, "/api/v1/order-items").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/order-items/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/order-items/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/v1/order-items/**").hasAnyRole("ADMIN", "USER")

            .anyRequest().authenticated()
        )
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
