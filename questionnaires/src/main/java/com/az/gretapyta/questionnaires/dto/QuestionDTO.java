package com.az.gretapyta.questionnaires.dto;

import com.az.gretapyta.qcore.dto.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
/// @AllArgsConstructor
public class QuestionDTO extends BaseDTO {

  @NotNull
  private Integer userId;

  @NotNull
  @Size(max = 16)
  private String code;

  private String title;
  private Map<String, String> titleMultilang; /// = new TreeMap<>();

  /// @NotNull
  private String questionAsked;
  private Map<String, String> questionAskedMultilang; /// = new TreeMap<>();


  @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String langCode;

  @NotNull
  private Integer displayOrder;

  @NotNull
  private String answerType;

  @NotNull
  private boolean ready2Show = false;

  private List<OptionDTO> options;

  //----/ Business Logic section: /-------------------------------//

  //----/ Business Logic section: /-------------------------------//
}