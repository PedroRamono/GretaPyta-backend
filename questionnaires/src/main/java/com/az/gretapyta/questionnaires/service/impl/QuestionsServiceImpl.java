package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.*;
import com.az.gretapyta.questionnaires.repository.QuestionnaireStepsRepository;
import com.az.gretapyta.questionnaires.repository.QuestionsRepository;
import com.az.gretapyta.questionnaires.repository.StepQuestionsRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import com.az.gretapyta.questionnaires.service.QuestionsService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionsServiceImpl extends BaseServiceImpl implements QuestionsService {
  private final StepQuestionsRepository stepQuestionRepository;
  private final QuestionnaireStepsRepository questionnaireStepRepository;
  private final QuestionsRepository repository;

  private final QuestionnairesService questionnairesService;
  private final UsersService usersService;

  @Override
  public List<Question> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<Question> getAllItems(int userId) {
    if (usersService.isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<Question> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Question> list = repository.findAll(specOr, Sort.by("code"));

      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public List<Question> getItemsByParentId(Integer parentId) {
    Specification<StepQuestionLink> specParentId = GenericSpecification.getParentIdSpecs(
        parentId,
        "stepDown");

    // List<StepQuestionLink> stepQuestionLink = stepQuestionRepository.findAll(StepQuestionLinkSpecification.withParentStepId(parentId));
    List<StepQuestionLink> stepQuestionLink = stepQuestionRepository.findAll(specParentId);
    List<Question> ret = new ArrayList<>(stepQuestionLink.size());
    for (StepQuestionLink n : stepQuestionLink) {
      Question question = repository.findById(n.getQuestionUp().getId()).orElseThrow(NotFoundException::new);
      ret.add(question);
    }
    return ret;
  }

  @Override
  public List<Question> getItemsByParentId(Integer parentId, int userId) {
    List<Question> list = getItemsByParentId(parentId);
    Predicate<Question> tester = n -> (n.getReady2Show() || (n.getUser().getId() == userId));
    return list.stream()
        .filter(tester).toList();
  }

  @Override
  public Question getItemByIdNoUserFilter(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Question getItemById(Integer id, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByIdNoUserFilter(id);
    } else {
      Specification<Question> specAndOr = GenericSpecification.getIdAndReady2ShowOrOwnerUserSpecs(id, userId);
      Question item = repository.findOne(specAndOr).orElseThrow(NotFoundException::new);
      item.filterChildrenOnReady2Show(false, userId);
      return item;
    }
  }

  @Override
  public Optional<Question> getItemByCodeNoUserFilter(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Optional<Question> getItemByCode(String code, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByCodeNoUserFilter(code);
    } else {
      Specification<Question> specAndOr = GenericSpecification.geCodeAndReady2ShowOrOwnerUserSpecs(code, userId);
      Optional<Question> item = repository.findOne(specAndOr); //.orElseThrow(NotFoundException::new);
      item.ifPresent(n -> n.filterChildrenOnReady2Show(false, userId));
      return item;
    }
  }

  @Override
  public Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    if (lexiconName.equalsIgnoreCase("type")) {
      Set<LexiconItem> ret = new LinkedHashSet<>();
      EnumSet.allOf(AnswerTypes.class)
          .forEach(n -> ret.add(new LexiconItem(n.getCode(), n.getLabel(langCode)))
          );
      return ret;
    }
    return Collections.emptySet(); // default.
  }

  @Override
  public Question createEntity(Question entity, String lang) throws BusinessException {
    if (validateBeforeCreate(entity, lang)) {
      return repository.save(entity);
    }
    return null;
  }

  @Override
  public Question updateEntity(Question entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
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
  // @Override
  protected boolean validateBeforeCreate(Question entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      if (codeExists(entity.getCode())) {
        log.error("Create Question failed: Code '{}' exists already!", entity.getCode());
        String localeMess = CommonUtilities.getTranslatableMessage("error.code_exists_already", lang);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(entity.getCode()));
      }
      else return true;
    }
    return false;
  }

  @Override
  public boolean codeExists(String code) {
    return (getItemByCodeNoUserFilter(code).isPresent());
  }

  protected void validateBeforeCreateEntityParentLink( Step step,
                                                       Question question ) throws BusinessException {

    Specification<QuestionnaireStepLink> specParentId =
    GenericSpecification.getChildIdKeyValueSpecs("stepId", step.getId(), "id");

    List<QuestionnaireStepLink> linkList = questionnaireStepRepository.findAll(specParentId);

    if (linkList.isEmpty()) return;

    String newQuestionCode = question.getCode();

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

  protected boolean validateBeforeUpdate(Question entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      Question existingEntity = getItemByIdNoUserFilter(entity.getId());
      validate4PortOutOnUpdate(existingEntity, entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOutOnUpdate(Question originalEntity, Question destinationEntity) {
    destinationEntity.setCreated(originalEntity.getCreated());
    destinationEntity.setCode(originalEntity.getCode()); // Code is not allowed to be modified.
    // Dependants
    destinationEntity.setSteps(originalEntity.getSteps());
    destinationEntity.setStepQuestion(originalEntity.getStepQuestion());
    destinationEntity.setOptions(originalEntity.getOptions());
    destinationEntity.setQuestionOptions(originalEntity.getQuestionOptions());
  }
  //----/ Business Logic section: /-------------------------------//
}