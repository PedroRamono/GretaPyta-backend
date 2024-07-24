package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;

import java.util.List;
import java.util.Optional;

public interface QuestionsService {
  List<Question> getAllItems();
  List<Question> getItemsByParentId(Integer parentId);
  Question getItemById(Integer id);
  Optional<Question> getItemByCode(String code);
  boolean isQuestionOfCodeExistsAlready(String code);

  Question createEntity(Question entity, String lang) throws BusinessException;

  StepQuestionLink saveStepQuestion( Step step,
                                     Question question,
                                     int displayOrder,
                                     int tenantId ) throws BusinessException;
}