package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.jpa.QuestionnaireStepLinkSpecification;
import com.az.gretapyta.questionnaires.jpa.StepQuestionLinkSpecification;
import com.az.gretapyta.questionnaires.model.*;
import com.az.gretapyta.questionnaires.repository.QuestionnaireStepsRepository;
import com.az.gretapyta.questionnaires.repository.QuestionsRepository;
import com.az.gretapyta.questionnaires.repository.StepQuestionsRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import com.az.gretapyta.questionnaires.service.QuestionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionsServiceImpl extends BaseServiceImpl implements QuestionsService {
  private final StepQuestionsRepository stepQuestionRepository;
  private final QuestionnaireStepsRepository questionnaireStepRepository;
  private final QuestionsRepository repository;

  private final QuestionnairesService questionnairesService;

  @Override
  public List<Question> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<Question> getItemsByParentId(Integer parentId) {
    List<StepQuestionLink> stepQuestionLink = stepQuestionRepository.findAll(StepQuestionLinkSpecification.withParentStepId(parentId));
    List<Question> ret = new ArrayList<>(stepQuestionLink.size());
    for (StepQuestionLink n : stepQuestionLink) {
      Question question = repository.findById(n.getQuestionUp().getId()).orElseThrow(NotFoundException::new);
      ret.add(question);
    }
    return ret;
  }

  @Override
  public Question getItemById(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Optional<Question> getItemByCode(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Question createEntity(Question entity, String lang) throws BusinessException {
    if (validateBeforeCreate(entity, lang)) {
      return repository.save(entity);
    }
    return null;
  }

  @Override
  public StepQuestionLink saveStepQuestion(Step step,
                                           Question question,
                                           int displayOrder,
                                           int tenantId) throws BusinessException {
    int stepId = step.getId();
    int questionId = question.getId();
    if ( (stepId <= 0) || (questionId <= 0)) {
      log.error("Cannot load StepQuestion: StepId = {}, QuestionId = {} !", stepId, questionId);
      return null;
    }

    validateBeforeCreateEntityParentLink(step, question);

    StepQuestionLink q = new StepQuestionLink(
        new StepQuestionLinkKey(stepId, questionId),
        step,
        question,
        displayOrder,
        tenantId);
    return stepQuestionRepository.save(q);
  }

  public static boolean isChildOfParent(Question entity, int parentId) {
    Set<StepQuestionLink> questionnaireSteps = entity.getStepQuestion();
    for (StepQuestionLink m : questionnaireSteps) {
      if (m.getStepDown().getId() == parentId) return true;
    }
    return false;
  }

  //----/ Business Logic section: /-------------------------------//
  @Override
  protected <Question extends BaseEntity> boolean validateBeforeCreate(Question entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      String code = ((com.az.gretapyta.questionnaires.model.Question) entity).getCode();
      if (isQuestionOfCodeExistsAlready(code)) {
        log.error("Create Question failed: duplicated '{}' code.", code);
        String localeMess = CommonUtilities.getTranslatableMessage("error_create_entity_failed", Constants.DEFAULT_LOCALE);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(code));
      } else return true;
    }
    return false;
  }

  @Override
  public boolean isQuestionOfCodeExistsAlready(String code) {
    return (getItemByCode(code).isPresent());
  }

  protected void validateBeforeCreateEntityParentLink( Step step,
                                                       Question question ) throws BusinessException {
    String newQuestionCode = question.getCode();
    //(1) Find all links where given Step is involved:
    List<QuestionnaireStepLink> linkList =
        questionnaireStepRepository.findAll(QuestionnaireStepLinkSpecification.hasSteps(step.getId()));

    if (linkList.isEmpty()) return;

    //(2) Check all Questionnaires with the given Step if they have Question with the code already:
    for (QuestionnaireStepLink n : linkList) {
      int questionnaireId = n.getId().getQuestionnaireId();
      if (questionnairesService.isQuestionOfCodeInQuestionnaire(questionnaireId, newQuestionCode)) {
          log.error("Create StepQuestionLink failed: Question with code '{}' already exists in Questionnaire with ID={}", newQuestionCode , questionnaireId);
          String localeMess = CommonUtilities.getTranslatableMessage("error_create_step_question_link_failed", Constants.DEFAULT_LOCALE);
          assert localeMess != null;
          throw new BusinessException(localeMess.formatted(newQuestionCode, questionnaireId));
      }
    }
  }
  //----/ Business Logic section: /-------------------------------//
}