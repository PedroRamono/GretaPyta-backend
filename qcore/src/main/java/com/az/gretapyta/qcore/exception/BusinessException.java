package com.az.gretapyta.qcore.exception;

import com.az.gretapyta.qcore.model.BaseEntity;
import lombok.Getter;

@Getter
public class BusinessException extends Exception {
  private String whatReason = "";
  private transient Class<? extends BaseEntity> entity;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(Class<? extends BaseEntity> entity) {
    this.entity = entity;
  }

  public BusinessException(Throwable cause) {
    super(cause.getMessage());
  }

  public BusinessException(String message, Class<? extends BaseEntity> entity) {
    super(message);
    this.entity = entity;
  }

  public BusinessException(String message, String whatReason) {
    super(message);
    this.whatReason = whatReason;
  }

  public BusinessException(String message, String whatReason, Class<? extends BaseEntity> entity) {
    super(message);
    this.whatReason = whatReason;
    this.entity = entity;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("BusinessException ")
      .append(getMessage().isEmpty() ? "" : "message: " + getMessage()).append("; ")
      .append(getWhatReason().isEmpty() ? "" : "reason: " + getWhatReason() +"; \n")
      .append((getEntity() == null) || getEntity().toString().isEmpty() ? "" : "Entity: " + getEntity() +".");
    return sb.toString();
  }
}