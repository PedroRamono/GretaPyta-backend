package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Questionnaire;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnairesService {
  List<Questionnaire> getAllItems();
  List<Questionnaire> getAllItems(int userId);

  List<Questionnaire> getItemsByParentId(Integer parentId, int userId);

  Questionnaire getItemByIdNoUserFilter(Integer id);
  Questionnaire getItemById(Integer id, int userId);

  Optional<Questionnaire> getItemByCodeNoUserFilter(String code);
  Optional<Questionnaire> getItemByCode(String code, int userId);

  Questionnaire findByNameMultilangFirstLike(String pattern);

  Optional<Questionnaire> getItemByUrlIdNameNoUserFilter(String urlIdName);
  Optional<Questionnaire> getItemByUrlIdName(String urlIdName, int userId);

  Set<Question> getAllQuestionsForQuestionnaire(Integer questionnaireId);
  Set<Question> getAllQuestionsForQuestionnaire(Integer questionnaireId, int userId);
  // Set<Question> getAllQuestionsForStepInQuestionnaire(Integer stepId, Integer questionnaireId);
  boolean isQuestionOfCodeInQuestionnaire(final Integer questionnaireId, final String questionCode);
  boolean codeExists(final String code);
  boolean urlNameExists(String urlIdName);
  // Lexicons specific for the Entity:
  Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode);

  Questionnaire createEntity(Questionnaire entity, String lang) throws BusinessException;
  Questionnaire updateEntity(Questionnaire entity, String lang) throws BusinessException;
}