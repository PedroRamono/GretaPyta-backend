package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;

import java.util.List;
import java.util.Optional;

public interface UserQuestionnairesService {
  List<UserQuestionnaire> getAllItems();

  //TODO ... List<UserQuestionnaire> getAllItems(int page, int size, String sortDir, String sort); //TODO ...

  List<UserQuestionnaire> getItemsByUserId(Integer userId);
  Optional<UserQuestionnaire> getItemsByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId);
  Optional<UserQuestionnaire> getItemsByUserIdAndQuestionnaireCode(Integer userId, String questionnaireCode);

  UserQuestionnaire getItemById(Integer id);

  UserQuestionnaire createEntity(UserQuestionnaire entity, String lang) throws BusinessException;
}