package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.jpa.QuestionOptionLinkSpecification;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import com.az.gretapyta.questionnaires.model.QuestionOptionLinkKey;
import com.az.gretapyta.questionnaires.repository.OptionsRepository;
import com.az.gretapyta.questionnaires.repository.QuestionOptionsRepository;
import com.az.gretapyta.questionnaires.service.OptionsService;
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
public class OptionsServiceImpl extends BaseServiceImpl implements OptionsService {
  private final QuestionOptionsRepository questionOptionRepository;
  private final OptionsRepository repository;

  @Override
  public List<Option> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<Option> getItemsByParentId(Integer parentId) {
    /*
    Predicate<Option> tester = d -> (isChildOfParent(d, parentId));
    return getEntitiesAttributeFromPredicate(repository.findAll(), tester);
    */
    List<QuestionOptionLink> questionOptionLink =
        questionOptionRepository.findAll(QuestionOptionLinkSpecification.withParentQuestionId(parentId));
    List<Option> ret = new ArrayList<>(questionOptionLink.size());
    for (QuestionOptionLink n : questionOptionLink) {
      ret.add( repository.findById(n.getOption().getId()).orElseThrow(NotFoundException::new));
    }
    return ret;
  }

  @Override
  public Option getItemById(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Optional<Option> getItemByCode(String code) {
    return repository.findByCode(code);
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

  public boolean isChildOfParent(Option entity, int parentId) {
    Set<QuestionOptionLink> parentChildrenLink = entity.getQuestionOptions();
    for (QuestionOptionLink m : parentChildrenLink) {
      if (m.getQuestionDown().getId() == parentId) return true;
    }
    return false;
  }

  /*
  private static List<Option> getEntitiesAttributeFromPredicate( List<Option> entities,
                                                                 Predicate<Option> tester ) { // Function<User, String> mapper
    return entities.stream()
        .filter(tester).toList();
    // .findFirst()
    // .map(mapper)
  }
  */
}