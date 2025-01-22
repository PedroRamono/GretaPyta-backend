package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import com.az.gretapyta.questionnaires.dto2.UserQuestionnaireDTO;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.model2.UserQuestionnaire;
import com.az.gretapyta.questionnaires.service.QuestionnairesService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserQuestionnaireMapper { // interface
  @Autowired
  QuestionnairesService questionnairesService;
  @Autowired
  UsersService usersService;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answerLang", target = "answerLang")
  @Mapping(source = "user.id", target = "userDTO")
  @Mapping(source = "questionnaireUser.id", target = "questionnaireDTO")
  @Mapping(source = "ipAddressFrom", target = "ipAddressFrom")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  public abstract UserQuestionnaireDTO map(UserQuestionnaire entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answerLang", target = "answerLang")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  @Mapping(source = "questionAnswersDTO", target = "questionAnswers")
  @Mapping(source = "ipAddressFrom", target = "ipAddressFrom")
  public abstract UserQuestionnaire map(UserQuestionnaireDTO dto);

  @AfterMapping
  public void afterChildMapping(@MappingTarget UserQuestionnaireDTO dto, UserQuestionnaire entity) {
    // UserQuestionnaire's status conversion:
    EnumCommon enumObj = EnumCommon.getEnumFromCode(UserQuestionnaireStatuses.values(), entity.getCompletionStatus());
    if (enumObj != null) {
      dto.setCompletionStatus(((UserQuestionnaireStatuses)enumObj)); // cast to UserQuestionnaireStatuses.
    } else {
      // Validation - failure
      //TODO ... Validation
    }
  }

  @AfterMapping
  public void afterChildMapping(@MappingTarget UserQuestionnaire entity, UserQuestionnaireDTO dto) {
    // GenderType's type conversion:
    if (dto.getCompletionStatus() != null) {
      entity.setCompletionStatus(dto.getCompletionStatus().getCode());
    } else {
      // Validation - failure
      //TODO ... Validation // log.warn
    }

    int userId = dto.getUserDTO();
    int questionnaireId = dto.getQuestionnaireDTO();

    User user = usersService.getItemById(userId); //TODO ... checking against NPE, handling it
    Questionnaire questionnaire = questionnairesService.getItemByIdNoUserFilter(questionnaireId); //TODO ... checking against NPE, handling it

    entity.setUser(user);
    entity.setQuestionnaireUser(questionnaire);
  }
}