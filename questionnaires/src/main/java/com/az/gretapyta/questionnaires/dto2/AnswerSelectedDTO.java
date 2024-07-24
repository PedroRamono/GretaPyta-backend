package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AnswerSelectedDTO extends BaseDTO {
  private Integer questionAnswerDTO;
  private Integer optionDTO;
}