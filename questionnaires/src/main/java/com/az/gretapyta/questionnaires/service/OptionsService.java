package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.Question;

import com.az.gretapyta.questionnaires.model.QuestionOptionLink;

import java.util.List;
import java.util.Optional;

public interface OptionsService {

  List<Option> getAllItems();
  List<Option> getItemsByParentId(Integer parentId);
  Option getItemById(Integer id);
  Optional<Option> getItemByCode(String code);

  Option createEntity(Option entity, String lang) throws BusinessException;
  QuestionOptionLink saveQuestionOption( Question question,
                                         Option option,
                                         int displayOrder,
                                         int tenantId );
}