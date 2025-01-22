package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import com.az.gretapyta.questionnaires.repository2.QuestionAnswersRepository;
import com.az.gretapyta.questionnaires.service.QuestionsService;
import com.az.gretapyta.questionnaires.service2.QuestionAnswersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionAnswersServiceImpl extends BaseServiceImpl implements QuestionAnswersService {
  private final QuestionAnswersRepository repository;

  private final QuestionsService questionsService;

  @Override
  public List<QuestionAnswer> getAllItems() {
    return repository.findAll();
  }

  @Override
  public QuestionAnswer getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  public List<QuestionAnswer> getAllItemsByUserQuestionnaireId(Integer userQuestionnaireId) {

    Specification<QuestionAnswer> specUserQuestionnaireId = GenericSpecification.getParentIdSpecs(
        userQuestionnaireId,
        "userQuestionnaire");

    return repository.findAll(specUserQuestionnaireId);
  }

  public Optional<QuestionAnswer> getItemByUserQuestionnaireIdAndQuestionId( Integer userQuestionnaireId,
                                                                             Integer questionId ) {

    Specification<QuestionAnswer> specUserQuestionnaireId = GenericSpecification.getParentIdSpecs(
        userQuestionnaireId,
        "userQuestionnaire");

    Specification<QuestionAnswer> specQuestionId = GenericSpecification.getParentIdSpecs(
        questionId,
        "question");

    return repository.findAll(
        specUserQuestionnaireId.and(specQuestionId))
        .stream()
        .findFirst();
  }

  @Override
  public QuestionAnswer createEntity(QuestionAnswer entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  //----/ Business Logic section: /-------------------------------//
  //TODO ... on Update
  // @Override
  protected <QuestionAnswer extends BaseEntity> boolean validateBeforeUpdate(QuestionAnswer entity) throws BusinessException {
    /*
    if (super.validateBeforeUpdate(entity)) {
    checkForExistingAnswerAlreadyForTypeSingleton( Question question,
        com.az.gretapyta.questionnaires.model2.QuestionAnswer questionAnswer ) throws BusinessException {
    }
    */
    return true;
  }

  @Override
  protected <QuestionAnswer extends BaseEntity> boolean validateBeforeCreate(QuestionAnswer entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      Question question = questionsService.getItemByIdNoUserFilter(
          ((com.az.gretapyta.questionnaires.model2.QuestionAnswer) entity).getQuestion().getId());
      if (AnswerTypes.isOfUserInputType(question.getAnswerType())) {
        return validateForUserInputType((com.az.gretapyta.questionnaires.model2.QuestionAnswer) entity, question);
      } else {
        return validateForSelectionBasedType((com.az.gretapyta.questionnaires.model2.QuestionAnswer) entity, question);
      }
    }
    return true;
  }

  private boolean validateForUserInputType(QuestionAnswer questionAnswer, Question question) throws BusinessException {
    //(1)
    Set<AnswerSelected> answerSelected = questionAnswer.getAnswerSelections();
    if ( ! ((answerSelected==null) || answerSelected.isEmpty())) {
      String logMess = "===> Create AnswerProvided failed: Question with code '%s' and ID=%d is not of 'Select Answer(s)' type.";
      log.error(String.format(logMess, question.getCode(), question.getId()));
      String localeMess = CommonUtilities.getTranslatableMessage(
          "error_create_selected_answer_failed_wrong_question_type",
          questionAnswer.getUserQuestionnaire().getAnswerLang());
      assert localeMess != null;
      throw new BusinessException(localeMess.formatted(question.getCode()));
    }
    return true;
  }

  private boolean validateForSelectionBasedType(QuestionAnswer questionAnswer, Question question) throws BusinessException {
    if (validateForAnswerSelectedType(questionAnswer, question)) {
      if ( ! AnswerTypes.isMultiSelectionChoice(question.getAnswerType())) {
        return checkForSelectionTypeSingleton(question, questionAnswer);
      }
    }
    return true;
  }

  private boolean validateForAnswerSelectedType(QuestionAnswer questionAnswer, Question question) throws BusinessException {
    AnswerProvided answerProvided = questionAnswer.getAnswerProvided();
    if ( ! ((answerProvided==null) || (answerProvided.getAnswer()==null))) {
      String logMess = "===> Create AnswerSelected failed: Question with code '%s' and ID=%d is not of 'Provided Answer' type but answer is provided.";
      log.error(String.format(logMess, question.getCode(), question.getId()));
      String localeMess = CommonUtilities.getTranslatableMessage(
          "error_create_provided_answer_failed_wrong_question_type",
          questionAnswer.getUserQuestionnaire().getAnswerLang());
      assert localeMess != null;
      throw new BusinessException(localeMess.formatted(question.getCode()));
    }
    return true;
  }

  private boolean checkForSelectionTypeSingleton( Question question,
                                                  QuestionAnswer questionAnswer ) throws BusinessException {

    if (questionAnswer.getAnswerSelections() == null) return true;

    // More than one Selected Answer in answers set ?
    if (questionAnswer.getAnswerSelections().stream().toList().size() > 1) {
      String logMess = "===> Create AnswerSelected failed: Question with code '%s' and ID=%d is of single-selection type but multiple choices provided.";
      log.error(String.format(logMess, question.getCode(), question.getId()));
      String localeMess = CommonUtilities.getTranslatableMessage(
          "error_create_selected_answer_failed_multiple_selected_answers",
          questionAnswer.getUserQuestionnaire().getAnswerLang());
      assert localeMess != null;
      throw new BusinessException(localeMess.formatted(question.getCode()));
    }
    return true;
  }
  //----/ Business Logic section: /-------------------------------//
}