package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLinkKey;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.repository.QuestionnaireStepsRepository;
import com.az.gretapyta.questionnaires.repository.StepsRepository;
import com.az.gretapyta.questionnaires.service.StepsService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
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
  private final UsersService usersService;

  @Override
  public List<Step> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<Step> getAllItems(int userId) {
    if (usersService.isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<Step> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Step> list = repository.findAll(specOr);

      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public List<Step> getAllItemsFiltered(String fieldName, String txtFilter, int userId, String langCode) {
    List<Step> ret;
    if (usersService.isAdministrator(userId)) {
      ret = repository.findAll()
          .stream()
          .filter(d -> isPatternInJsonByLang(d.getNameMultilang(), txtFilter, langCode)).toList();
    } else {
      Specification<Step> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      ret = repository.findAll(specOr)
          .stream()
          .filter(d -> isPatternInJsonByLang(d.getNameMultilang(), txtFilter, langCode)).toList();

      ret.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
    }

    //TODO... by Specifications:
    /*
    final SearchCriteriaOnMap searchCriteria = new SearchCriteriaOnMap( fieldName,
        txtFilter,
        SearchOperation.MATCH_IN_LANG_MAP,
        langCode);
    GenericSpecification<Step> txtFilterSpec = new GenericSpecification<Step>(searchCriteria);
    ret = repository.findAll(txtFilterSpec);
    */

    return ret;
  }

  @Override
  public Step getItemByIdNoUserFilter(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Step getItemById(Integer id, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByIdNoUserFilter(id);
    } else {
      Specification<Step> specAndOr = GenericSpecification.getIdAndReady2ShowOrOwnerUserSpecs(id, userId);
      Step item = repository.findOne(specAndOr).orElseThrow(NotFoundException::new);
      item.filterChildrenOnReady2Show(false, userId);
      return item;
    }
  }

  @Override
  public List<Step> getItemsByParentId(Integer parentId) {
    Predicate<Step> tester = d -> (isChildOfParent(d, parentId));
    return getEntitiesAttributeFromPredicate(repository.findAll(), tester);
  }

  @Override
  public List<Step> getItemsByParentId(Integer parentId, int userId) {
    List<Step> list = getItemsByParentId(parentId);
    /// Predicate<Step> tester = Step::getReady2Show;
    Predicate<Step> tester = n -> (n.getReady2Show() || (n.getUser().getId() == userId));
    return list.stream()
        .filter(tester).toList();
  }

  public Optional<Step> findByNameMultilangFirstLike(String pattern, int userId) {
    if (usersService.isAdministrator(userId)) {
      return findByNameMultilangFirstLike(pattern);
    } else {
      Optional<Step> step = findByNameMultilangFirstLike(pattern);
      if (step.isPresent()) {
        if (step.get().getReady2Show() || (step.get().getUser().getId() == userId)) {
          return step;
        }
      }
      return Optional.empty();
    }
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
  public Step updateEntity(Step entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }

  protected boolean validateBeforeUpdate(com.az.gretapyta.questionnaires.model.Step entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      Step existingEntity = getItemByIdNoUserFilter(entity.getId());
      validate4PortOutOnUpdate(existingEntity, entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOutOnUpdate(Step originalEntity, Step destinationEntity) {
    destinationEntity.setCreated(originalEntity.getCreated());
    // Dependants
    destinationEntity.setQuestionnaires(originalEntity.getQuestionnaires());
    destinationEntity.setQuestionnaireStep(originalEntity.getQuestionnaireStep());
    destinationEntity.setQuestionsUp(originalEntity.getQuestionsUp());
    destinationEntity.setStepQuestions(originalEntity.getStepQuestions());
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