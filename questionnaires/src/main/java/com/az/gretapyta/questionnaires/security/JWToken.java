package com.az.gretapyta.questionnaires.security;

import java.io.Serial;
import java.io.Serializable;

public record JWToken(String name, String contentHash) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}