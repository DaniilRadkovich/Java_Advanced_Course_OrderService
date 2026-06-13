package com.innowise.orderservice.exception;

public class EntityValidationException extends RuntimeException {
  public EntityValidationException(String message) {
    super(message);
  }
}
