package com.az.gretapyta.qcore.model;

import java.io.Serial;
import java.io.Serializable;

public record LexiconItem(String code, String name) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
}