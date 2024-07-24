package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.enums.AnswerTypes;
import com.az.gretapyta.qcore.enums.EnumCommon;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.OptionDTO;
import com.az.gretapyta.questionnaires.dto.QuestionDTO;
import com.az.gretapyta.questionnaires.model.Question;
import com.az.gretapyta.questionnaires.model.StepQuestionLink;
import com.az.gretapyta.questionnaires.service2.AnswersSelectedService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.az.gretapyta.questionnaires.mapper.StepMapper.DEFAULT_DISPLAY_ORDER;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper {

  @Autowired
  protected OptionMapper optionMapper;

  @Autowired
  protected AnswersSelectedService answersSelectedService;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  @Mapping(source = "ready2Show", target = "ready2Show")
  public abstract QuestionDTO map(Question entity);

  @BeanMapping(ignoreByDefault = true) // ignoreByDefault = false)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "titleMultilang", target = "titleMultilang")
  @Mapping(source = "questionAskedMultilang", target = "questionAskedMultilang")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "ready2Show", target = "ready2Show")
  public abstract Question map(QuestionDTO dto);

  public QuestionDTO mapWithLang(Question entity, String langCode) {
    QuestionDTO dto = map(entity);

    //(1)
    dto.setLangCode(langCode);
   //(2)
    dto.setTitle(CommonUtilities.getTranslatedText(entity.getTitleMultilang(), langCode, "title"));
    dto.setQuestionAsked(CommonUtilities.getTranslatedText(entity.getQuestionAskedMultilang(), langCode, "questionAsked"));

    //(4)
    // AnswerTypes type conversion:
    String enumCode = entity.getAnswerType();
    if ( ! ((enumCode == null) || enumCode.isEmpty())) {
      EnumCommon enumCommon = EnumCommon.getEnumFromCode(AnswerTypes.values(), enumCode);
      if (enumCommon != null) {
        dto.setAnswerType((AnswerTypes) enumCommon); // cast to AnswerTypes.
      }
    }

    //(5) Options:
    List<OptionDTO> listDto =
        entity.getOptions().stream().map(p -> optionMapper.mapWithLang(p, langCode)).toList();

    //(6)
    populatePopularityCounts(entity.getId(), listDto);

    //(7)
    dto.setOptions( listDto
            .stream()
            .sorted(Comparator.comparing(OptionDTO::getDisplayOrder))
            .toList());

    dto.setDisplayOrder(StepMapper.DEFAULT_DISPLAY_ORDER); // some (not-null) default is needed.

    return dto;
  }

  public QuestionDTO mapForParentWithLang(Question entity, int parentId, String langCode) {
    QuestionDTO dto = mapWithLang(entity, langCode);
    establishDisplayOrder(entity, dto, parentId);
    return dto;
  }

  private static void establishDisplayOrder(Question entity, QuestionDTO dto, int parentId) {
    // Display order - from link entity
    dto.setDisplayOrder( entity.getStepQuestion()
        .stream()
        .filter(d -> d.getStepDown().getId() == parentId)
        .findFirst()
        .map(StepQuestionLink::getDisplayOrder).orElseGet(() -> DEFAULT_DISPLAY_ORDER));
  }

  private void populatePopularityCounts(int question_id, List<OptionDTO> list) {
    Map<Integer, Integer> popularityMap = answersSelectedService.getOptionsPopularityCounts(question_id, false);

    if ((popularityMap == null) || popularityMap.isEmpty()) {
      return;
    }

    for (OptionDTO n : list) {
      Integer count = popularityMap.get(n.getId());
      n.setStatsCntr(count == null ? 0 : count);
    }
  }

  @AfterMapping
  public static void afterChildMapping(@MappingTarget Question entity, QuestionDTO dto) {
    // AnswerTypes type conversion:
    if (dto.getAnswerType() != null) {
      entity.setAnswerType(dto.getAnswerType().getCode());
    }
  }
}