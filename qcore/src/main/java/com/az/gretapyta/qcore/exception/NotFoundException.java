package com.az.gretapyta.qcore.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException() {
    super();
  }

  public NotFoundException(final String message) {
    super( message);
  }
}