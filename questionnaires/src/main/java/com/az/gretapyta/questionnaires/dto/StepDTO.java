package com.az.gretapyta.questionnaires.dto;

import com.az.gretapyta.qcore.dto.BaseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@EqualsAndHashCode(callSuper = true)
@Data
public class StepDTO extends BaseDTO {

  @NotNull
  private Integer userId;

  // @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String name;
  private Map<String, String> nameMultilang; // = new TreeMap<>();

  @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String langCode = DEFAULT_LOCALE;

  @NotNull
  private Integer displayOrder;

  @NotNull
  private boolean ready2Show = false;

  private List<QuestionDTO> questions; // private List<Question> questions;

  //----/ Business Logic section: /-------------------------------//
  //----/ Business Logic section: /-------------------------------//
}