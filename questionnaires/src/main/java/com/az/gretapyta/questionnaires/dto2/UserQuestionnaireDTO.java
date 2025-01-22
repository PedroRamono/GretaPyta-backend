package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import com.az.gretapyta.qcore.enums.UserQuestionnaireStatuses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserQuestionnaireDTO extends BaseDTO {

  @Size(max = 4)
  private String answerLang;

  private String ipAddressFrom;

  @NotNull
  private UserQuestionnaireStatuses completionStatus;

  private Integer userDTO; // was UserDTO

  private Integer questionnaireDTO; // was QuestionnaireDTO

  private List<QuestionAnswerDTO> questionAnswersDTO;
}