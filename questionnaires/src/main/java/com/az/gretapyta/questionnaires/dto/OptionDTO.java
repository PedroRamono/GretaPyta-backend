package com.az.gretapyta.questionnaires.dto;

import com.az.gretapyta.qcore.dto.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@EqualsAndHashCode(callSuper = true)
@Data
// @AllArgsConstructor
public class OptionDTO extends BaseDTO {

  @NotNull
  private Integer userId;

  @NotNull
  @Size(max = 16)
  private String code;

  private String name;
  private String help;

  /// @NotNull
  private Map<String, String> nameMultilang; // = new TreeMap<>();
  private Map<String, String> helpMultilang; // = new TreeMap<>();

  @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String langCode = DEFAULT_LOCALE;

  @NotNull
  private boolean ready2Show = false;

  private boolean preferredAnswer;

  @NotNull
  private Integer displayOrder;

  private Integer statsCntr;

  //----/ Business Logic section: /-------------------------------//

  //----/ Business Logic section: /-------------------------------//
}