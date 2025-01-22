package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.model.LexiconItem;
import com.az.gretapyta.questionnaires.model2.UserMessage;

import java.util.List;
import java.util.Set;

public interface UserMessagesService {
  List<UserMessage> getAllItems();
  List<UserMessage> getItemsByUserId(Integer userId);
  UserMessage getItemById(Integer id);

  // Lexicons specific for the Entity:
  Set<LexiconItem> getLexiconForCode(String lexiconName, String langCode);

  UserMessage createEntity(UserMessage entity, String lang) throws BusinessException;

  // Currently not allowed - might change>
  // UserMessage updateEntity(UserMessage entity, String lang) throws BusinessException;
}