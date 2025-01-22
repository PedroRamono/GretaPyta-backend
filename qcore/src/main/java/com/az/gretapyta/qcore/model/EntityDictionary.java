package com.az.gretapyta.qcore.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EntityDictionary implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String entityName;

  @Setter
  private Map<String, DictionaryEntry> map;

  public EntityDictionary(String entityName) {
    this.entityName = entityName;
    this.map = new HashMap<>();
  }

  public void add(String attribName, DictionaryEntry entry) {
    map.put(attribName, entry);
  }
}