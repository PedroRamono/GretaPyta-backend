package com.az.gretapyta.questionnaires.mapper;

import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.dto.DrawerDTO;
import com.az.gretapyta.questionnaires.dto.QuestionnaireDTO;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.service.DrawersService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class DrawerMapper {
  @Autowired
  protected QuestionnaireMapper questionnaireMapper;
  @Autowired
  protected DrawersService drawersService;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  ///  @Mapping(source = "nameMultilang", target = "nameMultilang")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "created", target = "created")
  public abstract DrawerDTO map(Drawer entity);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "code", target = "code")
  @Mapping(source = "nameMultilang", target = "nameMultilang")
  @Mapping(source = "ready2Show", target = "ready2Show")
  @Mapping(source = "created", target = "created")
  public abstract Drawer map(DrawerDTO dto);

  public DrawerDTO mapWithLang(Drawer entity, String langCode) {
    DrawerDTO ret = map(entity);
    //(2)
    ret.setLangCode(langCode);
    ret.setName(CommonUtilities.getTranslatedText(entity.getNameMultilang(), langCode, "name"));
    //(3)
    List<QuestionnaireDTO> list =
        entity.getQuestionnaires().stream().map(p -> questionnaireMapper.mapWithLang(p, langCode)).toList();
    //(4)
    populatePopularityCounts(list);

    ret.setQuestionnaires(list);
    return ret;
  }

  private void populatePopularityCounts(List<QuestionnaireDTO> list) {
    Map<Integer, Integer> popularityMap = drawersService.getQuestionnairesPopularityCounts(false);

    if ((popularityMap == null) || popularityMap.isEmpty()) { return; }

    for (QuestionnaireDTO n : list) {
      Integer count = popularityMap.get(n.getId());
      n.setStatsCntr(count == null ? 0 : count);
    }
  }
}