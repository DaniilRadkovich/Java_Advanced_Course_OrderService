package com.innowise.orderservice.exception;

public class InvalidOrderQuantityException extends RuntimeException {

  public InvalidOrderQuantityException(String message) {
    super(message);
  }
}
