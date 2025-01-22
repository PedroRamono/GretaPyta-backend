package com.az.gretapyta.questionnaires.service2;

import com.az.gretapyta.qcore.model.EntityDictionary;

import java.util.List;
import java.util.Optional;

/* To provide Locale translations
 * currently using i18n Java resources, but might be from DB in the future.
*/
public interface LocaleDictService {
  public List<EntityDictionary> getAllEntitiesDictionary(String langCode);

  public Optional<EntityDictionary> getEntityDictionary(String entityName, String langCode);
}