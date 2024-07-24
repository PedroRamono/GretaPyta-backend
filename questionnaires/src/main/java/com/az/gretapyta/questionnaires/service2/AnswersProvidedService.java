package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;

import java.util.List;
import java.util.Optional;

public interface AnswersProvidedService {
  List<AnswerProvided> getAllItems();
  Optional<AnswerProvided> getItemByQuestionAnswerId(Integer questionAnswerId);
  AnswerProvided getItemById(Integer id);

  AnswerProvided createEntity(AnswerProvided entity, String lang) throws BusinessException;
}