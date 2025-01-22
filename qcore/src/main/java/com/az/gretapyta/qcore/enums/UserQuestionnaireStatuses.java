package com.az.gretapyta.qcore.enums;

import com.az.gretapyta.qcore.util.Constants;

import java.util.Map;

import static com.az.gretapyta.qcore.util.Constants.DEFAULT_LOCALE;
import static java.util.Map.entry;

public enum UserQuestionnaireStatuses implements EnumCommon {
  COMPLETED("CPL",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Completed"),
          entry(Constants.LOCALE_KEY_PL, "Zakończony"),
          entry(Constants.LOCALE_KEY_RU, "Завершенный") )
  ),
  SAVED("SVD",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Saved - unfinished"),
          entry(Constants.LOCALE_KEY_PL, "Zapisany - niedokończony"),
          entry(Constants.LOCALE_KEY_RU, "Сохранено - незакончено") )
  ),
  ABANDONED("ABD",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Abandoned"),
          entry(Constants.LOCALE_KEY_PL, "Porzucony"),
          entry(Constants.LOCALE_KEY_RU, "Заброшенный") )
  ),
  UNKNOWN("NNN",
      Map.ofEntries(
          entry(Constants.LOCALE_KEY_EN, "Unknown"),
          entry(Constants.LOCALE_KEY_PL, "Nieznany"),
          entry(Constants.LOCALE_KEY_RU, "Неизвестный") )
  );

  private final String code;
  private final Map<String, String> nameMultilang;

  UserQuestionnaireStatuses(String code, Map<String, String> nameMultilang) {
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