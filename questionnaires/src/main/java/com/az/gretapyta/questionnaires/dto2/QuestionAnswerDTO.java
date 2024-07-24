package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class QuestionAnswerDTO extends BaseDTO {
  private Integer userQuestionnaireDTO; //AZ909-4 was UserQuestionnaireDTO
  private Integer questionDTO; //AZ909-4 was QuestionDTO

  private List<AnswerSelectedDTO> answerSelectionsDTO;
  private AnswerProvidedDTO answerProvidedDTO;
}