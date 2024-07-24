package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.jpa.QuestionnaireSpecification;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.repository.QuestionnairesRepository;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
// @AllArgsConstructor
public class QuestionnairesServiceImpl extends BaseServiceImpl implements QuestionnairesService {
  private final QuestionnairesRepository repository;

  @Override
  public List<Questionnaire> getAllItems() {
    return repository.findAll(Sort.by("code"));
  }

  @Override
  public List<Questionnaire> getItemsByParentId(Integer parentId) {
    return repository.findAll(QuestionnaireSpecification.withParentId(parentId)).stream().toList();
  }

  @Override
  public Questionnaire getItemById(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Optional<Questionnaire> getItemByCode(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Questionnaire getItemByUrlIdName(String urlIdName) {
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
    Questionnaire questionnaire = getItemById(questionnaireId);
    return questionnaire.getAllQuestions();
  }

  @Transactional(readOnly = true)
  public Set<Question> getAllQuestionsForStepInQuestionnaire(Integer stepId, Integer questionnaireId) {
    Questionnaire questionnaire = getItemById(questionnaireId);
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
    return (getItemByCode(code).isPresent());
  }

  @Override
  public Questionnaire createEntity(Questionnaire entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }
}