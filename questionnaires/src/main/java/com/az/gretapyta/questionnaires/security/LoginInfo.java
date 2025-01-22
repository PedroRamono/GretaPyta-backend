package com.az.gretapyta.questionnaires.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record LoginInfo(String loginName, String pin, String newPin) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public LoginInfo {
    Objects.requireNonNull(loginName,"'loginName' must not be null");
    if (loginName.isEmpty()) {
      throw new IllegalArgumentException("'loginName' must not be empty");
    }
    Objects.requireNonNull(pin,"Password must not be null");
    if (pin.isEmpty()) {
      throw new IllegalArgumentException("Password must not be empty");
    }
    // Don't restrict newPin
  }
}