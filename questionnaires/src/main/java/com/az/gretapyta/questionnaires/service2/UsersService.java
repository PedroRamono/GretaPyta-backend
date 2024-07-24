package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.questionnaires.model2.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {
  List<User> getAllItems();
  User getItemById(Integer id);

  User getItemByAnonymousFlag(String langCode);
  Optional<User> getUserByLoginName(String loginName);
  List<User> getUserByFirstLastNameFirst(String fName, String lName);

  boolean isLoginNameExistsAlready(String loginName);

  User createEntity(User entity, String lang) throws BusinessException;
}