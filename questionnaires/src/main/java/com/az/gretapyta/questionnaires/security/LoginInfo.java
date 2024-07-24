package com.az.gretapyta.questionnaires.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record LoginInfo(String loginName, String pin) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public LoginInfo {
    Objects.requireNonNull(loginName,"'loginName' must not be null");
    if (loginName.isEmpty()) {
      throw new IllegalArgumentException("'loginName' must not be empty");
    }
    Objects.requireNonNull(pin,"'pin' must not be null");
    if (pin.isEmpty()) {
      throw new IllegalArgumentException("'pin' must not be empty");
    }
  }
}