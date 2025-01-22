package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.dto.StepDTO;
import com.az.gretapyta.questionnaires.model.QuestionnaireStepLink;
import com.az.gretapyta.questionnaires.model.Step;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import com.az.gretapyta.questionnaires.service2.UsersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StepMapper {
  public final static int DEFAULT_DISPLAY_ORDER = 1;
  @Autowired
  protected QuestionMapper questionMapper;

  @Autowired
  UsersService usersService;

  @BeanMapping(ignoreByDefault = true) // ignoreByDefault = false)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "user.id", target = "userId")
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

    //(4) Questions, do sorting:
    List<QuestionDTO> sortedQuestionsDto = entity.getStepQuestions()
        .stream()
        .sorted(Comparator.comparing(StepQuestionLink::getDisplayOrder))
        .map(p -> {
          QuestionDTO n = questionMapper.mapWithLang(p.getQuestionUp(), langCode);
              n.setDisplayOrder(p.getDisplayOrder());
              return n;
            }
        )
        .toList();
    dto.setQuestions(sortedQuestionsDto);

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

  @AfterMapping
  public void afterChildMapping(@MappingTarget Step entity, StepDTO dto) {
    entity.setUser(usersService.getItemById(dto.getUserId()));
  }
}