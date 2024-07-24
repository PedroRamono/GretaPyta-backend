package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionnairesService {
  List<Questionnaire> getAllItems();
  List<Questionnaire> getItemsByParentId(Integer parentId);
  Questionnaire getItemById(Integer id);
  Optional<Questionnaire> getItemByCode(String code);
  Questionnaire findByNameMultilangFirstLike(String pattern);

  Questionnaire getItemByUrlIdName(String urlIdName);

  Set<Question> getAllQuestionsForQuestionnaire(Integer questionnaireId);
  Set<Question> getAllQuestionsForStepInQuestionnaire(Integer stepId, Integer questionnaireId);
  boolean isQuestionOfCodeInQuestionnaire(final Integer questionnaireId, final String questionCode);

  boolean codeExists(final String code);

  Questionnaire createEntity(Questionnaire entity, String lang) throws BusinessException;
}