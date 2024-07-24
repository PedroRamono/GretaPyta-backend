package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.questionnaires.dto2.QuestionAnswerDTO;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import com.az.gretapyta.questionnaires.service.QuestionsService;
import com.az.gretapyta.questionnaires.service2.AnswersProvidedService;
import com.az.gretapyta.questionnaires.service2.UserQuestionnairesService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class QuestionAnswerMapper {

  @Autowired
  UserQuestionnairesService userQuestionnairesService;
  @Autowired
  QuestionsService questionsService;
  @Autowired
  AnswersProvidedService answersProvidedService;
  @Autowired
  AnswerProvidedMapper answerProvidedMapper;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "userQuestionnaire.id", target = "userQuestionnaireDTO")
  @Mapping(source = "question.id", target = "questionDTO")
  @Mapping(source = "answerProvided", target = "answerProvidedDTO")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  public abstract QuestionAnswerDTO map(QuestionAnswer entity);


  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answerProvidedDTO", target = "answerProvided")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  public abstract QuestionAnswer map(QuestionAnswerDTO dto);

  @AfterMapping
  public void afterChildMapping(@MappingTarget QuestionAnswerDTO dto, QuestionAnswer entity) {
    // Find (if exists), assign AnswerProvidedDTO:
    Optional<AnswerProvided> answerProvidedOptional = answersProvidedService.getItemByQuestionAnswerId(dto.getId()) ;
    answerProvidedOptional.ifPresent(answerProvided -> dto.setAnswerProvidedDTO(answerProvidedMapper.map(answerProvided)));
  }

  @AfterMapping
  public void afterChildMapping(@MappingTarget QuestionAnswer entity, QuestionAnswerDTO dto) {
    int userQuestionnaireId = dto.getUserQuestionnaireDTO(); //.getId();
    int questionId = dto.getQuestionDTO(); // .getId();

    UserQuestionnaire userQuestionnaire = userQuestionnairesService.getItemById(userQuestionnaireId);
    Question question = questionsService.getItemById(questionId); //TODO ... checking against NPE, handling it  !!!!!!

    entity.setUserQuestionnaire(userQuestionnaire);
    entity.setQuestion(question);
  }
}