package com.az.gretapyta.questionnaires.mapper2;

import com.az.gretapyta.questionnaires.dto2.UserDTO;
import com.az.gretapyta.questionnaires.model2.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "middleName", target = "middleName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "emailAddress", target = "emailAddress")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "loginName", target = "loginName")
  @Mapping(source = "preferredLang", target = "preferredLang")
  @Mapping(source = "anonymousUser", target = "anonymousUser")
  // @Mapping(source = "userQuestionnaires", target = "userQuestionnairesDTO")
  @Mapping(source = "created", target = "created")
  //
  @Mapping(source = "age", target = "age")
  UserDTO map(User entity);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "middleName", target = "middleName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "emailAddress", target = "emailAddress")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "loginName", target = "loginName")
  @Mapping(source = "passwordHash", target = "passwordHash")
  @Mapping(source = "preferredLang", target = "preferredLang")
  @Mapping(source = "anonymousUser", target = "anonymousUser")
  @Mapping(source = "created", target = "created")
  @Mapping(source = "updated", target = "updated")
  User map(UserDTO dto);

  @AfterMapping
  static void afterChildMapping(@MappingTarget UserDTO dto, User entity) {
    //(1) GenderType
    dto.setGender(entity.getGender());

    //(2) UserRole
    dto.setRole(entity.getRole());
  }

  @AfterMapping
  static void afterChildMapping(@MappingTarget User entity, UserDTO dto) {
    //(1) GenderType's type conversion:
    if (dto.getGender() != null) {
      entity.setGender(dto.getGender());
    }

    //(2) UserRole's type conversion:
    if (dto.getRole() != null) {
      entity.setRole(dto.getRole());
    }
  }
}