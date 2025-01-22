package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import com.az.gretapyta.questionnaires.model2.GenericValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerProvidedDTO extends BaseDTO {
  private Integer questionAnswerDTO; //AZ was QuestionAnswerDTO
  private GenericValue answer;
}