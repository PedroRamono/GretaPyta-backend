package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.Step;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StepMapper {
  public final static int DEFAULT_DISPLAY_ORDER = 1;
  @Autowired
  protected QuestionMapper questionMapper;

  @BeanMapping(ignoreByDefault = true) // ignoreByDefault = false)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  // @Mapping(source = "nameMultilang", target = "nameMultilang")
  public abstract StepDTO map(Step entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "nameMultilang", target = "nameMultilang")
  public abstract Step map(StepDTO entity);

  public StepDTO mapWithLang(Step entity, String langCode) {
    StepDTO dto = map(entity);

    //(1)
    // Take care of language-sensitive items
    dto.setLangCode(langCode);

    //(2)
    dto.setName(CommonUtilities.getTranslatedText(entity.getNameMultilang(), langCode, "name"));

    //(3)
    dto.setDisplayOrder( entity
        .getQuestionnaireStep()
        .stream()
        .findFirst()
        .map(QuestionnaireStepLink::getDisplayOrder).orElseGet(() -> DEFAULT_DISPLAY_ORDER));

    //(4)
    if ( ! (entity.getQuestionsUp() == null)) {
      List<QuestionDTO> list =
          entity.getQuestionsUp().stream().map(p -> questionMapper.mapWithLang(p, langCode)).toList();
      dto.setQuestions(list
          .stream()
          .sorted(Comparator.comparing(QuestionDTO::getDisplayOrder))
          .toList());
    }
    return dto;
  }

  public StepDTO mapForParentWithLang(Step entity, int parentId, String langCode) {
    StepDTO dto = mapWithLang(entity, langCode);
    establishDisplayOrder(entity, dto, parentId);
    return dto;
  }

  private static void establishDisplayOrder(Step entity, StepDTO dto, int parentId) {
    // Display order - from link entity
    dto.setDisplayOrder( entity.getQuestionnaireStep()
        .stream()
        .filter(d -> d.getQuestionnaire().getId() == parentId)
        .findFirst()
        .map(QuestionnaireStepLink::getDisplayOrder).orElseGet(() -> DEFAULT_DISPLAY_ORDER));
  }
}