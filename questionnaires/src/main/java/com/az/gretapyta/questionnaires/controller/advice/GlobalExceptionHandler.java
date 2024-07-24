package com.az.gretapyta.questionnaires.controller.advice;

import com.az.gretapyta.qcore.advice.ErrorResponse;
import com.az.gretapyta.qcore.controller.advice.BaseGlobalExceptionHandler;
import com.az.gretapyta.qcore.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

  @Override
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException exception) {
    return super.handleNotFound(exception);
  }

  @Override
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(
      final ResponseStatusException exception) {
    return super.handleResponseStatus(exception);
  }

  @Override
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(final NullPointerException exception) {
    return super.handleNotFound(exception);
  }

  @Override
  @ExceptionHandler(value = Throwable.class)
  public ResponseEntity<ErrorResponse> handleThrowable(final Throwable exception) {
    return super.handleThrowable(exception);
  }

  @Override
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(final BadCredentialsException exception) {
    return super.handleBadCredentials(exception);
  }
}