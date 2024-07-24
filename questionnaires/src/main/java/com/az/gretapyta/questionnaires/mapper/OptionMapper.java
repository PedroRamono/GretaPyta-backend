package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.model.Option;
import com.az.gretapyta.questionnaires.model.QuestionOptionLink;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class OptionMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  /// @Mapping(source = "nameMultilang", target = "nameMultilang")
  /// @Mapping(source = "helpMultilang", target = "helpMultilang")
  @Mapping(source = "preferredAnswer", target = "preferredAnswer")
  public abstract OptionDTO map(Option entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "nameMultilang", target = "nameMultilang")
  @Mapping(source = "helpMultilang", target = "helpMultilang")
  @Mapping(source = "preferredAnswer", target = "preferredAnswer")
  public abstract Option map(OptionDTO dto);

  public OptionDTO mapWithLang(Option entity, String langCode) {
    OptionDTO dto = map(entity);
    //(1)
    dto.setLangCode(langCode);
    //(2)
    dto.setName(CommonUtilities.getTranslatedText(entity.getNameMultilang(), langCode, "name"));
    dto.setHelp(CommonUtilities.getTranslatedText(entity.getHelpMultilang(), langCode, "help"));
    dto.setDisplayOrder(StepMapper.DEFAULT_DISPLAY_ORDER); // some (not-null) default is needed.
    return dto;
  }

  public OptionDTO mapForParentWithLang(Option entity, int parentId, String langCode) {
    OptionDTO dto = mapWithLang(entity, langCode);
    establishDisplayOrder(entity, dto, parentId);
    return dto;
  }

  private static void establishDisplayOrder(Option entity, OptionDTO dto, int parentId) {
    // Display order - from link entity
    dto.setDisplayOrder( entity
        .getQuestionOptions()
        .stream()
        .filter(d -> d.getQuestionDown().getId() == parentId)
        .findFirst()
        .map(QuestionOptionLink::getDisplayOrder).orElseGet(() -> StepMapper.DEFAULT_DISPLAY_ORDER));
  }
}