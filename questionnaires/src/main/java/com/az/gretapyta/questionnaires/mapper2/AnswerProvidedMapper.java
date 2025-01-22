package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.questionnaires.dto2.AnswerProvidedDTO;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;
import com.az.gretapyta.questionnaires.service2.QuestionAnswersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AnswerProvidedMapper {
  @Autowired
  QuestionAnswersService questionAnswersService;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answer", target = "answer")
  @Mapping(source = "questionAnswer.id", target = "questionAnswerDTO")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  public abstract AnswerProvidedDTO map(AnswerProvided entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answer", target = "answer")
  @Mapping(source = "created", target = "created")
  public abstract AnswerProvided map(AnswerProvidedDTO dto);

  @AfterMapping
  public void afterChildMapping(@MappingTarget AnswerProvidedDTO dto, AnswerProvided entity) {
    //TODO ...
  }

  @AfterMapping
  public void afterChildMapping(@MappingTarget AnswerProvided entity, AnswerProvidedDTO dto) {
    int questionAnswerId = dto.getQuestionAnswerDTO(); //.getId();
    QuestionAnswer questionAnswer = questionAnswersService.getItemById(questionAnswerId); //TODO ... checking against NPE, handling it  !!!!!!
    entity.setQuestionAnswer(questionAnswer);
  }
}