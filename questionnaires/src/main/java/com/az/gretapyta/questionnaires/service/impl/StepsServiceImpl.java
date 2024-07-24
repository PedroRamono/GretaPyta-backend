package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLinkKey;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.repository.QuestionnaireStepsRepository;
import com.az.gretapyta.questionnaires.repository.StepsRepository;
import com.az.gretapyta.questionnaires.service.StepsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Log4j2
@Service
@RequiredArgsConstructor
public class StepsServiceImpl extends BaseServiceImpl implements StepsService {
  private final QuestionnaireStepsRepository questionnaireStepRepository;

  private final StepsRepository repository;

  @Override
  public List<Step> getAllItems() {
    return repository.findAll();
  }

  @Override
  public Step getItemById(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public List<Step> getItemsByParentId(Integer parentId) {
    Predicate<Step> tester = d -> (isChildOfParent(d, parentId));
    return getEntitiesAttributeFromPredicate(repository.findAll(), tester);
  }

  @Override
  public Optional<Step> findByNameMultilangFirstLike(String pattern) {
    return repository.findAll()
        .stream()
        .filter(d -> isPatternInJson(d.getNameMultilang(), pattern))
        .findFirst();
        //.orElseThrow(NotFoundException::new);
  }

  @Override
  public Step createEntity(Step entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  @Override
  public QuestionnaireStepLink saveQuestionnaireStep(Questionnaire questionnaire,
                                                     Step step,
                                                     int displayOrder,
                                                     int tenantId) {
    int questionnaireId = questionnaire.getId();
    int stepId = step.getId();
    if ( (questionnaireId <= 0) || (stepId <= 0)) {
      log.error("Cannot load QuestionnaireStep: QuestionnaireId = {}, StepId = {} !", questionnaireId, stepId);
      return null;
    }
    QuestionnaireStepLink q = new QuestionnaireStepLink( new QuestionnaireStepLinkKey(questionnaireId, stepId),
                                                 questionnaire,
                                                 step,
                                                 displayOrder,
                                                 tenantId);
    return questionnaireStepRepository.save(q);
  }

  private static boolean isChildOfParent(Step step, int parentId) {
    Set <QuestionnaireStepLink> questionnaireSteps = step.getQuestionnaireStep();
    for (QuestionnaireStepLink m : questionnaireSteps) {
      if (m.getQuestionnaire().getId() == parentId) return true;
    }
    return false;
  }

  private static List<Step> getEntitiesAttributeFromPredicate( List<Step> entities,
                                                               Predicate<Step> tester ) { // Function<User, String> mapper
    return entities.stream()
        .filter(tester).toList();
  }
}