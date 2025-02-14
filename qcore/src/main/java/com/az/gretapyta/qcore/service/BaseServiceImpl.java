package com.az.gretapyta.qcore.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public abstract class BaseServiceImpl {
  public abstract List<? extends BaseEntity> getAllItems();

  protected <T extends BaseEntity> boolean validateBeforeCreate(T entity, String lang) throws BusinessException {
    entity.setCreated(LocalDateTime.now());
    entity.setUpdated(LocalDateTime.now());
    return true;
  }

  protected <T extends BaseEntity> boolean validateBeforeUpdate(T entity, String lang) throws BusinessException {
    entity.setUpdated(LocalDateTime.now());
    return true;
  }

  protected static boolean isPatternInJson(Map<String, String> map, String pattern) {
    for (Map.Entry<String, String> n : map.entrySet()) {
      if ((n != null) && (pattern != null) && n.getValue().contains(pattern)) {
        return true;
      }
    }
    return false;
  }

  protected static boolean isPatternInJsonByLang(Map<String, String> map, String pattern, String lang) {
    String valueByLang = map.get(lang);
    return (valueByLang != null && valueByLang.contains(pattern));
  }
}