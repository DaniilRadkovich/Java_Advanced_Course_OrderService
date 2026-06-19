package com.innowise.orderservice.exception.handler;

import com.innowise.orderservice.exception.CardLimitException;
import com.innowise.orderservice.exception.EntityNotFoundException;
import com.innowise.orderservice.exception.EntityValidationException;
import com.innowise.orderservice.exception.ErrorResponse;
import com.innowise.orderservice.exception.InvalidItemDataException;
import com.innowise.orderservice.exception.InvalidOrderDataException;
import com.innowise.orderservice.exception.InvalidOrderQuantityException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hibernate.NonUniqueResultException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityValidationException.class)
  public ResponseEntity<ErrorResponse> handleEntityValidationException(
      EntityValidationException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(CardLimitException.class)
  public ResponseEntity<ErrorResponse> handleCardLimitException(
      CardLimitException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
      EntityNotFoundException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Map<String, Object> errorResponse =
        Map.of(
            "Error response",
            "Validation entered arguments failed!",
            "Info",
            ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", ")));

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    String typeName =
        Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse("unknown");

    String message = String.format("Parameter '%s' should be of type %s!", ex.getName(), typeName);

    return ResponseEntity.status(status).body(message);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.CONFLICT;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(NonUniqueResultException.class)
  public ResponseEntity<ErrorResponse> handleNonUniqueResultException(
      NonUniqueResultException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.CONFLICT;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
      AuthorizationDeniedException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.FORBIDDEN;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(InvalidItemDataException.class)
  public ResponseEntity<ErrorResponse> handleInvalidItemDataException(
      InvalidItemDataException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(InvalidOrderDataException.class)
  public ResponseEntity<ErrorResponse> handleInvalidOrderDataException(
      InvalidOrderDataException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(InvalidOrderQuantityException.class)
  public ResponseEntity<ErrorResponse> handleInvalidOrderQuantityException(
      InvalidOrderQuantityException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
      OrderNotFoundException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProductNotFoundException(
      ProductNotFoundException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            ex.getClass().getSimpleName());

    return ResponseEntity.status(status).body(errorResponse);
  }
}
