package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserMessageDTO extends BaseDTO {
  private Integer userId;

  @Size(max = 1024)
  private String message;

  @Size(max = 4)
  private String visibilityLevel;

  private Integer forUserId;
}