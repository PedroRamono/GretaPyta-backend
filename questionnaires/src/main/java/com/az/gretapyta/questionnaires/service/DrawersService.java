package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Drawer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DrawersService {
  List<Drawer> getAllItems();
  Drawer getItemById(Integer id);
  Optional<Drawer> getItemByCode(String code);
  Map<Integer, Integer> getQuestionnairesPopularityCounts(Boolean byPopularity);
  boolean codeExists(final String code);

  Drawer createEntity(Drawer entity, String lang) throws BusinessException;
}