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

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@EqualsAndHashCode(callSuper = true)
@Data
public class DrawerDTO extends BaseDTO {

  @NotNull
  @Size(max = 16)
  private String code;

  private String name;

  /// @Getter(AccessLevel.NONE) // Don't broadcast it.
  private Map<String, String> nameMultilang; /// = new TreeMap<>();

  @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String langCode = DEFAULT_LOCALE;

  @NotNull
  private boolean ready2Show = false;

  private List<QuestionnaireDTO> questionnaires;

  //----/ Business Logic section: /-------------------------------//

}