package com.innowise.orderservice.exception;

public class InvalidItemDataException extends RuntimeException {

  public InvalidItemDataException(String message) {
    super(message);
  }
}
