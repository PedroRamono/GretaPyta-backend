package com.az.gretapyta.qcore.jpa;

import com.az.gretapyta.qcore.enums.SearchOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteriaOnMap extends SearchCriteriaBasic {
  private String langCode;

  public SearchCriteriaOnMap(String key, Object value, SearchOperation operation, String langCode) {
    super(key, value, operation);
    this.langCode = langCode;
  }
}