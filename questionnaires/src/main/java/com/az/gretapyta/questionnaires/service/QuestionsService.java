package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionsService {
  List<Question> getAllItems();
  List<Question> getAllItems(int userId);

  List<Question> getItemsByParentId(Integer parentId);
  List<Question> getItemsByParentId(Integer parentId, int userId);

  Question getItemByIdNoUserFilter(Integer id);
  Question getItemById(Integer id, int userId);

  Optional<Question> getItemByCodeNoUserFilter(String code);
  Optional<Question> getItemByCode(String code, int userId);

  boolean codeExists(String code);

  // Lexicons specific for the Entity:
  Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode);

  Question createEntity(Question entity, String lang) throws BusinessException;
  Question updateEntity(Question entity, String lang) throws BusinessException;

  StepQuestionLink saveStepQuestion( Step step,
                                     Question question,
                                     int displayOrder,
                                     int tenantId ) throws BusinessException;
}