package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.enums.QuestionnaireTypes;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.model.Questionnaire;
import com.az.gretapyta.questionnaires.service.DrawersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class QuestionnaireMapper {
  @Autowired
  protected DrawersService drawersService;
  @Autowired
  protected StepMapper stepMapper;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  /// @Mapping(source = "nameMultilang", target = "nameMultilang")
  /// @Mapping(source = "descriptionMultilang", target = "descriptionMultilang")
  @Mapping(source = "urlIdName", target = "urlIdName")
  @Mapping(source = "preferredLang", target = "preferredLang")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "commercialUsage", target = "commercialUsage")
  public abstract QuestionnaireDTO map(Questionnaire entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "nameMultilang", target = "nameMultilang")
  @Mapping(source = "descriptionMultilang", target = "descriptionMultilang")
  @Mapping(source = "urlIdName", target = "urlIdName")
  @Mapping(source = "preferredLang", target = "preferredLang")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "commercialUsage", target = "commercialUsage")
  @Mapping(source = "drawer", target = "drawer")
  public abstract Questionnaire map(QuestionnaireDTO dto);

  public QuestionnaireDTO mapWithLang(Questionnaire entity, String langCode) {
    QuestionnaireDTO dto = map(entity);

    //(1)
    dto.setLangCode(langCode);
    //(2)
    dto.setName(CommonUtilities.getTranslatedText(entity.getNameMultilang(), langCode, "name"));
    dto.setDescription(CommonUtilities.getTranslatedText(entity.getDescriptionMultilang(), langCode, "description"));

    //(3) Questionnaire's type conversion:
    EnumCommon qType = EnumCommon.getEnumFromCode(QuestionnaireTypes.values(), entity.getQuestionnaireType());
    if (qType != null) {
      dto.setQuestionnaireType((QuestionnaireTypes)qType); // cast to QuestionnaireTypes.
    }

    //(4)
    List<StepDTO> list = entity.getStepsUp().stream().map(p -> stepMapper.mapWithLang(p, langCode)).toList();
    if (! (list.isEmpty())) {
      dto.setSteps(list
          .stream()
          .sorted(Comparator.comparing(StepDTO::getDisplayOrder))
          .toList());
    }

    //(5) //TODO ... implement caching to avoid frequent calls to SQL function
    Map<Integer, Integer> popularityMap = drawersService.getQuestionnairesPopularityCounts(false);
    if ( ! ((popularityMap == null) || popularityMap.isEmpty())) {
      Integer count = popularityMap.get(dto.getId());
      dto.setStatsCntr(count == null ? 0 : count);
    }
    return dto;
  }

  @AfterMapping
  public void afterChildMapping(@MappingTarget Questionnaire entity, QuestionnaireDTO dto) {
    // QuestionnaireType type conversion:
    if (dto.getQuestionnaireType() != null) {
      entity.setQuestionnaireType(dto.getQuestionnaireType().getCode());
    }
  }
}