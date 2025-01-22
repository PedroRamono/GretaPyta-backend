package com.az.gretapyta.questionnaires.dto2.wizards;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TakeQuestionnaireDTO {
  @NotNull
  private Integer questionnaireId;
  @NotNull
  private String completionStatus;

  private String langCode;
  private List<TakeQuestionDTO> questionAnswers;
}