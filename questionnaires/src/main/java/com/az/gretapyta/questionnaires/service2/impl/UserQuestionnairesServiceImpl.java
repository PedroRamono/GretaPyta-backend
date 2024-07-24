package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.jpa2.UserQuestionnaireSpecification;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import com.az.gretapyta.questionnaires.repository2.UserQuestionnairesRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import com.az.gretapyta.questionnaires.service2.UserQuestionnairesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserQuestionnairesServiceImpl extends BaseServiceImpl implements UserQuestionnairesService {
  private final UserQuestionnairesRepository repository;

  private final QuestionnairesService questionnairesService;

  @Override
  public List<UserQuestionnaire> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<UserQuestionnaire> getItemsByUserId(Integer userId) {
    return repository.findAll(UserQuestionnaireSpecification.withUserId(userId));
  }

  @Override
  public Optional<UserQuestionnaire> getItemsByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId) {
    return repository.findAll(
        UserQuestionnaireSpecification.withUserId(userId)
        .and(UserQuestionnaireSpecification.withQuestionnaireId(questionnaireId))).stream().findFirst();
  }

  public Optional<UserQuestionnaire> getItemsByUserIdAndQuestionnaireCode(Integer userId, String questionnaireCode) {
    Optional<Questionnaire> optional = questionnairesService.getItemByCode(questionnaireCode);
    if (optional.isPresent()) {
      return getItemsByUserIdAndQuestionnaireId(userId, optional.get().getId());
    }
    return Optional.empty();
  }

  @Override
  public UserQuestionnaire getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  public UserQuestionnaire createEntity(UserQuestionnaire entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }
}