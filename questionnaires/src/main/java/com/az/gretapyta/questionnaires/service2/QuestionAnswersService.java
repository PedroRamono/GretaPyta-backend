package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;

import java.util.List;
import java.util.Optional;

public interface QuestionAnswersService {
  List<QuestionAnswer> getAllItems();
  QuestionAnswer getItemById(Integer id);

  List<QuestionAnswer> getAllItemsByUserQuestionnaireId(Integer userQuestionnaireId);
  Optional<QuestionAnswer> getItemByUserQuestionnaireIdAndQuestionId(Integer userQuestionnaireId, Integer questionId);

  QuestionAnswer createEntity(QuestionAnswer entity, String lang) throws BusinessException;
}