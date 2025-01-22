package com.az.gretapyta.questionnaires.dto2.wizards;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TakeQuestionDTO {
  @NotNull
  private Integer questionId;
  private String answerType;
  private List<Integer> answersSelectionIds;
  private String answerProvided;
}