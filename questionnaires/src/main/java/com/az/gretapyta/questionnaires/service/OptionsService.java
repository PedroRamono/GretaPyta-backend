package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;

import java.util.List;
import java.util.Optional;

public interface OptionsService {

  List<Option> getAllItems();
  List<Option> getAllItems(int userId);

  List<Option> getItemsByParentId(Integer parentId);
  List<Option> getItemsByParentId(Integer parentId, int userId);

  Option getItemByIdNoUserFilter(Integer id);
  Option getItemById(Integer id, int userId);

  Optional<Option> getItemByCodeNoUserFilter(String code);
  Optional<Option> getItemByCode(String code, int userId);

  boolean codeExists(String code);

  Option createEntity(Option entity, String lang) throws BusinessException;
  Option updateEntity(Option entity, String lang) throws BusinessException;

  QuestionOptionLink saveQuestionOption( Question question,
                                         Option option,
                                         int displayOrder,
                                         int tenantId );
}