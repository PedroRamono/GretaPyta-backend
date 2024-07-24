package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa2.AnswerSelectedSpecification;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import com.az.gretapyta.questionnaires.repository.QuestionsRepository;
import com.az.gretapyta.questionnaires.repository2.AnswersSelectedRepository;
import com.az.gretapyta.questionnaires.service2.AnswersSelectedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AnswersSelectedServiceImpl extends BaseServiceImpl implements AnswersSelectedService {
  private final AnswersSelectedRepository repository;

  private final QuestionsRepository questionsRepository;

  @Override
  public List<AnswerSelected> getAllItems() {
    return repository.findAll();
  }

  @Override
  public List<AnswerSelected> getItemsByQuestionAnswerId(Integer questionAnswerId) {
    return repository.findAll(AnswerSelectedSpecification.withQuestionAnswerId(questionAnswerId))
        .stream()
        .toList();
  }

  @Override
  public Optional<AnswerSelected> getItemByByQuestionAnswerIdAndOptionId(Integer questionAnswerId, Integer optionId) {
    return repository.findAll(
        AnswerSelectedSpecification.withQuestionAnswerId(questionAnswerId)
            .and(AnswerSelectedSpecification.withOptionId(optionId))).stream().findFirst();
  }

  @Override
  public AnswerSelected getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  public Map<Integer, Integer> getOptionsPopularityCounts(Integer question_id,  Boolean byPopularity) {
    try {
      Object[] rawArray = repository.getOptionsPopularityCounts(question_id, byPopularity);
      return CommonUtilities.convertRawArrayOfIdsToMap(rawArray);
    } catch (Exception e) { // InvalidDataAccessResourceUsageException
      log.error(e);
      return null;
    }
  }

  @Override
  public AnswerSelected createEntity(AnswerSelected entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  //----/ Business Logic section: /-------------------------------//
  //
  @Override
  protected <AnswerSelected extends BaseEntity> boolean validateBeforeCreate(AnswerSelected entity, String lang) throws BusinessException {
    if (super.validateBeforeCreate(entity, lang)) {
      int questionId =
          ((com.az.gretapyta.questionnaires.model2.AnswerSelected) entity).getQuestionAnswer().getQuestion().getId();
      Question question = questionsRepository.findById(questionId).orElseThrow(NotFoundException::new);
      if (checkForCorrectTypeOfParentQuestion(question, lang)) { // can throw BusinessException
        int questionAnswerId =
            ((com.az.gretapyta.questionnaires.model2.AnswerSelected) entity).getQuestionAnswer().getId();
        return checkForSiblingAnswerAlreadyForSingletonType(question, questionAnswerId, lang); // can throw BusinessException
      }
    }
    return false;
  }

  private static boolean checkForCorrectTypeOfParentQuestion( Question question,
                                                       String answerLang) throws BusinessException {
    String enumCode = question.getAnswerType();
    if (AnswerTypes.isOfUserInputType(enumCode)) {
      String logMess = "===> Create AnswerSelected failed: Question with code '%s' and ID=%d is not of 'Select Answer(s)' type.";
      log.error(String.format(logMess, question.getCode(), question.getId()));
      String localeMess = CommonUtilities.getTranslatableMessage("error_create_selected_answer_failed_wrong_question_type", answerLang);
      assert localeMess != null;
      throw new BusinessException(localeMess.formatted(question.getCode(), question.getId()));
    }
    return true;
  }

  public boolean checkForSiblingAnswerAlreadyForSingletonType( Question question,
                                                               int questionAnswerId,
                                                               String answerLang ) throws BusinessException {
    String questionAnswerTypeCode = question.getAnswerType();
    if ( ! AnswerTypes.isMultiSelectionChoice(questionAnswerTypeCode)) {
      // there should not be other, existing selection(s) for that Question already
      if (hasSelectedAnswerAlready(questionAnswerId)) {
        String logMess = "===> Create AnswerSelected failed: Question with code '%s' and ID=%d is of single-selection type and has the AnswerSelected already.";
        log.error(String.format(logMess, question.getCode(), question.getId()));
        String localeMess = CommonUtilities.getTranslatableMessage(
            "error_create_selected_answer_failed_sibling_exist_already_for_question_type", answerLang);
        assert localeMess != null;
        throw new BusinessException(localeMess.formatted(question.getCode(), question.getId()));
      }
    }
    return true;
  }

  public boolean hasSelectedAnswerAlready(int questionAnswerId) {
    List<AnswerSelected> list = getItemsByQuestionAnswerId(questionAnswerId);
    return ( ! ((list == null) || list.isEmpty()));
  }
  //----/ Business Logic section: /-------------------------------//
}