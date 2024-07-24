package com.az.gretapyta.questionnaires.dto;

import com.az.gretapyta.qcore.dto.BaseDTO;
import com.az.gretapyta.qcore.enums.QuestionnaireTypes;
import com.az.gretapyta.questionnaires.model.Drawer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;

@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data //AZ @Data creates problems when loading Many-to-Many !
//@Getter
//@Setter
/// @AllArgsConstructor
public class QuestionnaireDTO extends BaseDTO {
  /// private Integer id;

  @NotNull
  @Size(max = 16)
  private String code;

  @NotNull
  private QuestionnaireTypes questionnaireType;

  // @NotNull
  @Size(max = 64)
  private String urlIdName;

  @Size(max = 4)
  private String preferredLang;

  private String name;
  private Map<String, String> nameMultilang; /// = new TreeMap<>();

  private String description;
  private Map<String, String> descriptionMultilang; /// = new TreeMap<>();

  @Getter(AccessLevel.NONE) // Don't broadcast it.
  private String langCode = DEFAULT_LOCALE;

  @NotNull
  private boolean ready2Show = false;

  @NotNull
  private boolean commercialUsage = false;

  private Integer statsCntr;

  @NotNull
  private Drawer drawer; // private Drawer drawer;

  private List<StepDTO> steps; // Step Integer

  //----/ Business Logic section: /-------------------------------//

  //----/ Business Logic section: /-------------------------------//
}