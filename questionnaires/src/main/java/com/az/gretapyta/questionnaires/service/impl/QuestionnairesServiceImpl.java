package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.enums.QuestionnaireTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.repository.QuestionnairesRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
// @AllArgsConstructor
public class QuestionnairesServiceImpl extends BaseServiceImpl implements QuestionnairesService {

  private final QuestionnairesRepository repository;
  private final UsersService usersService;

  @Override
  public List<Questionnaire> getAllItems() {
    return repository.findAll(Sort.by("code"));
  }

  @Override
  public List<Questionnaire> getAllItems(int userId) {
    if (usersService.isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<Questionnaire> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Questionnaire> list = repository.findAll(specOr, Sort.by("code"));

      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public List<Questionnaire> getItemsByParentId(Integer parentId, int userId) {
    Specification<Questionnaire> specParentId = GenericSpecification.getParentIdSpecs(parentId, "drawer");
    if (usersService.isAdministrator(userId)) {
      return repository.findAll(specParentId).stream().toList();
    } else {
      Specification<Questionnaire> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Questionnaire> list = repository.findAll(specParentId.and(specOr)).stream().toList();
      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public Questionnaire getItemByIdNoUserFilter(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Questionnaire getItemById(Integer id, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByIdNoUserFilter(id);
    } else {
      Specification<Questionnaire> specAndOr = GenericSpecification.getIdAndReady2ShowOrOwnerUserSpecs(id, userId);
      Questionnaire item = repository.findOne(specAndOr).orElseThrow(NotFoundException::new);
      item.filterChildrenOnReady2Show(false, userId);

      return item;
    }
  }

  @Override
  public Optional<Questionnaire> getItemByCodeNoUserFilter(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Optional<Questionnaire> getItemByCode(String code, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByCodeNoUserFilter(code);
    } else {
      Specification<Questionnaire> specAndOr = GenericSpecification.geCodeAndReady2ShowOrOwnerUserSpecs(code, userId);
      Optional<Questionnaire> item = repository.findOne(specAndOr); //.orElseThrow(NotFoundException::new);
      item.ifPresent(n -> n.filterChildrenOnReady2Show(false, userId));
      return item;
    }
  }

  @Override
  public Optional<Questionnaire> getItemByUrlIdNameNoUserFilter(String urlIdName) {
    return repository.findByUrlIdName(urlIdName);
  }

  //TODO...UserId
  @Override
  public Optional<Questionnaire> getItemByUrlIdName(String urlIdName, int userId) {
    return repository.findByUrlIdName(urlIdName);
  }

  @Override
  public Questionnaire findByNameMultilangFirstLike(String pattern) {
    return repository.findAll()
        .stream()
        .filter(d -> isPatternInJson(d.getNameMultilang(), pattern))
        .findFirst()
        .orElseThrow(NotFoundException::new);
  }

  @Transactional(readOnly = true)
  public Set<Question> getAllQuestionsForQuestionnaire(Integer questionnaireId) {
    Questionnaire questionnaire = getItemByIdNoUserFilter(questionnaireId);
    return questionnaire.getAllQuestions();
  }

  //TODO...UserId
  @Transactional(readOnly = true)
  public Set<Question> getAllQuestionsForQuestionnaire(Integer questionnaireId, int userId) {
    Questionnaire questionnaire = getItemById(questionnaireId, userId);
    return questionnaire.getAllQuestions();
  }

  //TODO...UserId
  @Transactional(readOnly = true)
  public Set<Question> getAllQuestionsForStepInQuestionnaire(Integer stepId, Integer questionnaireId, int userId) {
    Questionnaire questionnaire = getItemById(questionnaireId, userId);
    Set<Step> stepSet = questionnaire.getStepsUp();
    return stepSet.iterator().next().getQuestionsUp();
  }

  @Transactional(readOnly = true)
  public boolean isQuestionOfCodeInQuestionnaire(final Integer questionnaireId, final String questionCode) {
    Set<Question> questionSet = getAllQuestionsForQuestionnaire(questionnaireId);
    if (questionSet==null || questionSet.isEmpty()) return false;
    for (Question n : questionSet) {
        if (questionCode.equalsIgnoreCase(n.getCode())) return true;
    }
    return false;
  }

  @Override
  public boolean codeExists(final String code) {
    return (getItemByCodeNoUserFilter(code).isPresent());
  }

  @Override
  public boolean urlNameExists(String urlIdName) {
    return (this.getItemByUrlIdNameNoUserFilter(urlIdName).isPresent());
  }

  @Override
  public Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode) {
    if (lexiconName.equalsIgnoreCase("type")) {
      Set<LexiconItem> ret = new LinkedHashSet<>();
      EnumSet.allOf(QuestionnaireTypes.class)
          .forEach(n -> ret.add(new LexiconItem(n.getCode(), n.getLabel(langCode)))
          );
      return ret;
    }
    return Collections.emptySet(); // default.
  }

  @Override
  public Questionnaire createEntity(Questionnaire entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  protected boolean validateBeforeCreate(Questionnaire entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      if (codeExists(entity.getCode())) {
        log.error("Create Questionnaire failed: Code '{}' exists already!", entity.getCode());
        String localeMess = CommonUtilities.getTranslatableMessage("error.code_exists_already", lang);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(entity.getCode()));
      } else if ((entity.getUrlIdName() != null) && urlNameExists(entity.getUrlIdName())) {
        log.error("Create Questionnaire failed: Associated Url '{}' exists already!", entity.getUrlIdName());
        String localeMess = CommonUtilities.getTranslatableMessage("error.associated_url_exists_already", lang);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(entity.getUrlIdName()));
      } else return true;
    }
    return false;
  }

  @Override
  public Questionnaire updateEntity(Questionnaire entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }

  protected boolean validateBeforeUpdate(Questionnaire entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      Questionnaire existingEntity = getItemById(entity.getId(), entity.getUser().getId());
      validate4PortOutOnUpdate(existingEntity, entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOutOnUpdate(Questionnaire originalEntity, Questionnaire destinationEntity) {
    destinationEntity.setCreated(originalEntity.getCreated());
    destinationEntity.setCode(originalEntity.getCode()); // Code is not allowed to be modified.
    // Dependants
    destinationEntity.setStepsUp(originalEntity.getStepsUp());
    destinationEntity.setQuestionnaireSteps(originalEntity.getQuestionnaireSteps());
    destinationEntity.setQestionnaireUsers(originalEntity.getQestionnaireUsers());
  }
}