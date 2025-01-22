package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.Step;

import java.util.List;
import java.util.Optional;

public interface StepsService {

  List<Step> getAllItems();
  List<Step> getAllItems(int userId); // Restricted by Ready-to-Show flag.
  List<Step> getAllItemsFiltered(String fieldName, String txtFilter, int userId, String langCode);

  List<Step> getItemsByParentId(Integer parentId);
  List<Step> getItemsByParentId(Integer parentId, int userId);

  Step getItemById(Integer id, int userId);
  Step getItemByIdNoUserFilter(Integer id);

  Optional<Step> findByNameMultilangFirstLike(String pattern, int userId);
  Optional<Step> findByNameMultilangFirstLike(String pattern);

  Step createEntity(Step entity, String lang) throws BusinessException;
  Step updateEntity(Step entity, String lang) throws BusinessException;

  QuestionnaireStepLink saveQuestionnaireStep(Questionnaire questionnaire, Step step, int displayOrder, int tenantId);
}