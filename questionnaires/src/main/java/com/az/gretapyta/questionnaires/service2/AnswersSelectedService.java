package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AnswersSelectedService {
  List<AnswerSelected> getAllItems();
  List<AnswerSelected> getItemsByQuestionAnswerId(Integer questionAnswerId);
  Optional<AnswerSelected> getItemByByQuestionAnswerIdAndOptionId(Integer questionAnswerId, Integer optionId);

  AnswerSelected getItemById(Integer id);

  Map<Integer, Integer> getOptionsPopularityCounts(Integer question_id, Boolean byPopularity);

  AnswerSelected createEntity(AnswerSelected entity, String lang) throws BusinessException;

  boolean checkForSiblingAnswerAlreadyForSingletonType( Question question,
                                                        int questionAnswerId,
                                                        String answerLang ) throws BusinessException;
}