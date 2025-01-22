package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import com.az.gretapyta.questionnaires.model.QuestionOptionLinkKey;
import com.az.gretapyta.questionnaires.repository.OptionsRepository;
import com.az.gretapyta.questionnaires.repository.QuestionOptionsRepository;
import com.az.gretapyta.questionnaires.service.OptionsService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Log4j2
@Service
@RequiredArgsConstructor
public class OptionsServiceImpl extends BaseServiceImpl implements OptionsService {
  private final QuestionOptionsRepository questionOptionRepository;
  private final OptionsRepository repository;
  private final UsersService usersService;

  @Override
  public List<Option> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<Option> getAllItems(int userId) {
    if (usersService.isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<Option> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Option> list = repository.findAll(specOr, Sort.by("code"));

      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public List<Option> getItemsByParentId(Integer parentId) {
    Specification<QuestionOptionLink> specParentId = GenericSpecification.getParentIdSpecs(
        parentId,
        "questionDown");

    List<QuestionOptionLink> questionOptionLink =
        // questionOptionRepository.findAll(QuestionOptionLinkSpecification.withParentQuestionId(parentId));
        questionOptionRepository.findAll(specParentId);
    List<Option> ret = new ArrayList<>(questionOptionLink.size());
    for (QuestionOptionLink n : questionOptionLink) {
      ret.add( repository.findById(n.getOption().getId()).orElseThrow(NotFoundException::new));
    }
    return ret;
  }

  @Override
  public List<Option> getItemsByParentId(Integer parentId, int userId) {
    List<Option> list = getItemsByParentId(parentId);
    Predicate<Option> tester = n -> (n.getReady2Show() || (n.getUser().getId() == userId));
    return list.stream()
        .filter(tester).toList();
  }

  @Override
  public Option getItemByIdNoUserFilter(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Option getItemById(Integer id, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByIdNoUserFilter(id);
    } else {
      Specification<Option> specAndOr = GenericSpecification.getIdAndReady2ShowOrOwnerUserSpecs(id, userId);
      Option item = repository.findOne(specAndOr).orElseThrow(NotFoundException::new);
      item.filterChildrenOnReady2Show(false, userId);
      return item;
    }
  }

  @Override
  public Optional<Option> getItemByCodeNoUserFilter(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Optional<Option> getItemByCode(String code, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByCodeNoUserFilter(code);
    } else {
      Specification<Option> specAndOr = GenericSpecification.geCodeAndReady2ShowOrOwnerUserSpecs(code, userId);
      Optional<Option> item = repository.findOne(specAndOr); //.orElseThrow(NotFoundException::new);
      item.ifPresent(n -> n.filterChildrenOnReady2Show(false, userId));
      return item;
    }
  }

  @Override
  public boolean codeExists(String code) {
    return (getItemByCodeNoUserFilter(code).isPresent());
  }

  @Override
  public QuestionOptionLink saveQuestionOption(Question question,
                                               Option option,
                                               int displayOrder,
                                               int tenantId) {
    int questionId = question.getId();
    int optionId = option.getId();
    if ( (questionId <= 0) || (optionId <= 0)) {
      log.error("Cannot load QuestionOption: QuestionId = {}, OptionId = {} !", questionId, optionId);
      return null;
    }
    QuestionOptionLink q = new QuestionOptionLink(
        new QuestionOptionLinkKey(questionId, optionId),
        question,
        option,
        displayOrder,
        tenantId);
    return questionOptionRepository.save(q);
  }

  @Override
  public Option createEntity(Option entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  @Override
  public Option updateEntity(Option entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }

  public boolean isChildOfParent(Option entity, int parentId) {
    Set<QuestionOptionLink> parentChildrenLink = entity.getQuestionOptions();
    for (QuestionOptionLink m : parentChildrenLink) {
      if (m.getQuestionDown().getId() == parentId) return true;
    }
    return false;
  }

  //----/ Business Logic section: /-------------------------------//
  // @Override
  protected boolean validateBeforeCreate(Option entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      if (codeExists(entity.getCode())) {
        log.error("Create Option failed: Code '{}' exists already!", entity.getCode());
        String localeMess = CommonUtilities.getTranslatableMessage("error.code_exists_already", lang);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(entity.getCode()));
      }
      else return true;
    }
    return false;
  }

  protected boolean validateBeforeUpdate(Option entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      Option existingEntity = getItemByIdNoUserFilter(entity.getId());
      validate4PortOutOnUpdate(existingEntity, entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOutOnUpdate(Option originalEntity, Option destinationEntity) {
    destinationEntity.setCreated(originalEntity.getCreated());
    destinationEntity.setCode(originalEntity.getCode()); // Code is not allowed to be modified.
  }
  //----/ Business Logic section: /-------------------------------//
}