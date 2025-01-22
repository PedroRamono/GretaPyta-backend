package com.az.gretapyta.qcore.jpa;

import com.az.gretapyta.qcore.enums.SearchOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteriaOnChild extends SearchCriteriaBasic {
  private String childEntityName; // To have access to child Entity.

  public SearchCriteriaOnChild(String key, Object value, SearchOperation operation, String childEntityName) {
    super(key, value, operation);
    this.childEntityName = childEntityName;
  }
}