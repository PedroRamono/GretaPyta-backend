package com.az.gretapyta.qcore.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record DictionaryEntry( String name,
                               String dataType4Client,
                               String keyi18n,
                               String caption,
                               String title,
                               String placeholder ) implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  /*
  name: string;
  keyi18n: string
  caption: string;
  title: string;
  placeholder: string;
  */

  public DictionaryEntry {
    Objects.requireNonNull(caption,"'text' must not be null");
    if (caption.isEmpty()) {
      throw new IllegalArgumentException("'text' must not be empty");
    }
  }
}