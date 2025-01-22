package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum VisibilityLevels implements EnumCommon {

  PRIVATE("PRV",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Private"),
          entry(Constants.LOCALE_KEY_PL, "Prywatna"),
          entry(Constants.LOCALE_KEY_RU, "Частный") )
  ),
  FOR_USERS("FUO",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "For USers Only"),
          entry(Constants.LOCALE_KEY_PL, "Tylko dla Użytkowników"),
          entry(Constants.LOCALE_KEY_RU, "Только для пользователей") )
  ),
  PUBLIC("PUB",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Public"),
          entry(Constants.LOCALE_KEY_PL, "Publiczna"),
          entry(Constants.LOCALE_KEY_RU, "Публичный") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  VisibilityLevels(String code, Map<String, String> nameMultilang) {
    this.code = code;
    this.nameMultilang = nameMultilang;
  }

  // Interface implementation part:
  @Override
  public String getCode() { return code; }

  @Override
  public String getLabel() {
    return getLabel(DEFAULT_LOCALE);
  }

  @Override
  public String getLabel(String langCode) {
    if (nameMultilang.containsKey(langCode)) {
      return (nameMultilang.get(langCode).isEmpty() ?
          getCode() :
          nameMultilang.get(langCode)); // To check/signal missing translation
    }
    return code;
  }
}