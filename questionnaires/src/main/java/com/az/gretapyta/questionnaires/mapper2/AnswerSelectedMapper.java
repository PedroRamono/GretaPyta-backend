package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.questionnaires.dto2.AnswerSelectedDTO;
import com.az.gretapyta.questionnaires.mapper.OptionMapper;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model2.AnswerSelected;
import com.az.gretapyta.questionnaires.model2.QuestionAnswer;

import com.az.gretapyta.questionnaires.service.OptionsService;
import com.az.gretapyta.questionnaires.service2.QuestionAnswersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AnswerSelectedMapper {

  @Autowired
  QuestionAnswersService questionAnswersService;
  @Autowired
  OptionsService optionsService;

  // Actual DTO objects provided.
  @Autowired
  QuestionAnswerMapper questionAnswerMapper;
  @Autowired
  OptionMapper optionMapper;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  public abstract AnswerSelectedDTO map(AnswerSelected entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  public abstract AnswerSelected map(AnswerSelectedDTO dto);

  @AfterMapping
  public void afterChildMapping(@MappingTarget AnswerSelectedDTO dto, AnswerSelected entity) {
  }

  @AfterMapping
  public void afterChildMapping(@MappingTarget AnswerSelected entity, AnswerSelectedDTO dto) {
    int questionAnswerId = dto.getQuestionAnswerDTO(); //.getId();
    int optionId = dto.getOptionDTO(); // .getId();//
    QuestionAnswer questionAnswer = questionAnswersService.getItemById(questionAnswerId);
    Option option = optionsService.getItemById(optionId);
    entity.setQuestionAnswer(questionAnswer);
    entity.setOptionAnswer(option);
  }
}