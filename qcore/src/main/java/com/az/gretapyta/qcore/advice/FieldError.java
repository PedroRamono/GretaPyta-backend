package com.az.gretapyta.qcore.advice;

import lombok.Data;

@Data
public class FieldError {
  private String field;
  private String errorCode;
}