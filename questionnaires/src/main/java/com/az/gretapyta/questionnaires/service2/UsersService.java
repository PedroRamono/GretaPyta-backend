package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.questionnaires.model2.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsersService {
  List<User> getAllItems();
  List<User> getAllItems(int userId);
  User getItemById(Integer id);

  User getItemByAnonymousFlag(String langCode);
  Optional<User> getUserByLoginName(String loginName);
  List<User> getUserByFirstLastNameFirst(String fName, String lName);

  boolean isLoginNameExistsAlready(String loginName);
  boolean isAdministrator(int userId);
  boolean isAdministrator4Demo(int userId);

  User createEntityNoAuthorityCheck(User entity, String lang) throws BusinessException;
  User createEntity(User entity, int userId, String lang) throws BusinessException;
  User updateEntity(User entity, int userId, String lang) throws BusinessException;
  void deleteEntity(int entityId, int userId, String lang) throws BusinessException;

  void updatePassword(String loginName, String newPassword);

  // Lexicons specific for the Entity:
  Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode);
}