package com.az.gretapyta.qcore.controller.advice;

import com.az.gretapyta.qcore.advice.ErrorResponse;
import com.az.gretapyta.qcore.advice.FieldError;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Log4j2
public abstract class BaseGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException exception) {
    exception.printStackTrace();
    log.error(exception.getMessage(), exception);
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setHttpStatus(HttpStatus.NOT_FOUND.value());
    errorResponse.setException(exception.getClass().getSimpleName());
    errorResponse.setMessage(exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException exception,
                                                                 HttpHeaders headers,
                                                                 HttpStatusCode status,
                                                                 WebRequest request ) {
    exception.printStackTrace();
    log.error(exception.getMessage(), exception);
    final BindingResult bindingResult = exception.getBindingResult();
    final List<FieldError> fieldErrors = bindingResult.getFieldErrors()
        .stream()
        .map(error -> {
          final FieldError fieldError = new FieldError();
          fieldError.setErrorCode(error.getCode());
          fieldError.setField(error.getField());
          return fieldError;
        })
        .toList();
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setException(exception.getClass().getSimpleName());
    errorResponse.setFieldErrors(fieldErrors);
    return new ResponseEntity<>(errorResponse, status); // HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<ErrorResponse> handleResponseStatus(
      final ResponseStatusException exception) {
    exception.printStackTrace();
    log.error(exception.getMessage(), exception);
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setHttpStatus(exception.getStatusCode().value());
    errorResponse.setException(exception.getClass().getSimpleName());
    errorResponse.setMessage(exception.getMessage());
    return new ResponseEntity<>(errorResponse, exception.getStatusCode());
  }

  public ResponseEntity<ErrorResponse> handleNotFound(final NullPointerException exception) {
    exception.printStackTrace();
    log.error(exception.getMessage(), exception);
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.setException(exception.getClass().getSimpleName());
    errorResponse.setMessage(exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public ResponseEntity<ErrorResponse> handleThrowable(final Throwable exception) {
    log.error(exception.getMessage(), exception);
    exception.printStackTrace();
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setMessage(exception.getMessage());
    int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    if (BusinessException.class.isInstance(exception)) {
      status = ((BusinessException)exception).getSuggestedHttpStatus() != null ?
          ((BusinessException)exception).getSuggestedHttpStatus() :
          status;
    }
    errorResponse.setHttpStatus(status);
    errorResponse.setException(exception.getClass().getSimpleName());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public ResponseEntity<ErrorResponse> handleBadCredentials(final BadCredentialsException exception) {
    exception.printStackTrace();
    log.error(exception.getMessage(), exception);
    final ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
    errorResponse.setException(exception.getClass().getSimpleName());
    errorResponse.setMessage(exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }
}