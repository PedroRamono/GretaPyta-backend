package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.questionnaires.dto2.UserMessageDTO;
import com.az.gretapyta.questionnaires.model2.User;
import com.az.gretapyta.questionnaires.model2.UserMessage;
import com.az.gretapyta.questionnaires.service2.UsersService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMessageMapper {
  @Autowired
  UsersService usersService;

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  @Mapping(source = "message", target = "message")
  @Mapping(source = "visibilityLevel", target = "visibilityLevel")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "forUserId", target = "forUserId")
  public abstract UserMessageDTO map(UserMessage entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  @Mapping(source = "message", target = "message")
  @Mapping(source = "visibilityLevel", target = "visibilityLevel")
  @Mapping(source = "forUserId", target = "forUserId")
  public abstract UserMessage map(UserMessageDTO dto);

  @AfterMapping
  public void afterChildMapping(@MappingTarget UserMessage entity, UserMessageDTO dto) {
    User user = usersService.getItemById(dto.getUserId());
    entity.setUser(user);
  }
}