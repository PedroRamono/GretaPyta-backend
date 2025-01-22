package com.az.gretapyta.qcore.jpa;

import com.az.gretapyta.qcore.enums.SearchOperation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteriaBasic {
  private String key;
  private Object value;
  private SearchOperation operation;

  public SearchCriteriaBasic(String key, Object value, SearchOperation operation) {
    this.key = key;
    this.value = value;
    this.operation = operation;
  }
}