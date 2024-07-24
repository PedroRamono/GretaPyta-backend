package com.az.gretapyta.questionnaires.model2;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record GenericValue(String valueType, String value) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  // private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

  public GenericValue {
    Objects.requireNonNull(valueType,"'valueType' must not be null");
    if (valueType.isEmpty()) {
      throw new IllegalArgumentException("'valueType' must not be empty");
    }
    Objects.requireNonNull(value,"'value' must not be null");
    if (value.isEmpty()) {
      throw new IllegalArgumentException("'value' must not be empty");
    }
  }
}